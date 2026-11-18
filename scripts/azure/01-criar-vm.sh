#!/bin/bash
# ============================================================
# NivelSeguro - 01) Provisionar a infraestrutura no Azure via Azure CLI
#
# Onde executar: Azure Cloud Shell (Bash)  ->  https://shell.azure.com
# Como executar:
#   chmod +x scripts/azure/01-criar-vm.sh
#   ./scripts/azure/01-criar-vm.sh
#
# Cria: Resource Group, VNet + Subnet, NSG (portas 22 e 8080),
#       IP publico e a VM Linux AlmaLinux 9.
# ============================================================
set -e

# ---------------- Variaveis ----------------
RESOURCE_GROUP="rg-nivelseguro"
LOCATION="chilecentral"
VM_NAME="vm-nivelseguro"
IMAGE="almalinux:almalinux-x86_64:9-gen2:latest"
SIZE="Standard_B2ats_v2"          # 2 vCPU / 1 GB RAM (o script 02 cria 2 GB de swap)
ADMIN_USERNAME="admlnx"
VNET_NAME="vnet-nivelseguro"
SUBNET_NAME="snet-nivelseguro"
NSG_NAME="nsg-nivelseguro"
PUBLIC_IP_NAME="pip-nivelseguro"
API_PORT="8080"
SHUTDOWN_TIME_UTC="0400"          # desligamento automatico as 01:00 (horario de Brasilia)

# A senha NAO fica gravada no repositorio: e pedida na execucao
# (ou pode ser passada antes: export ADMIN_PASSWORD='...').
if [ -z "$ADMIN_PASSWORD" ]; then
    echo "Senha do usuario $ADMIN_USERNAME da VM"
    echo "(12+ caracteres com maiuscula, minuscula, numero e simbolo - crie uma senha propria)"
    read -r -s -p "Senha: " ADMIN_PASSWORD
    echo ""
fi

echo "======================================================"
echo " NivelSeguro - provisionando infraestrutura no Azure"
echo "======================================================"

# ---------------- 1. Resource Group ----------------
echo ""
echo "[1/4] Criando o Resource Group: $RESOURCE_GROUP ($LOCATION)"
az group create \
    --name "$RESOURCE_GROUP" \
    --location "$LOCATION" \
    --tags projeto=NivelSeguro disciplina=DevOps \
    --output table

# ---------------- 2. VM + rede (VNet, Subnet, NSG, IP publico) ----------------
echo ""
echo "[2/4] Criando a VM $VM_NAME com VNet, Subnet, NSG e IP publico (pode levar alguns minutos)..."
az vm create \
    --resource-group "$RESOURCE_GROUP" \
    --name "$VM_NAME" \
    --location "$LOCATION" \
    --image "$IMAGE" \
    --size "$SIZE" \
    --authentication-type password \
    --admin-username "$ADMIN_USERNAME" \
    --admin-password "$ADMIN_PASSWORD" \
    --vnet-name "$VNET_NAME" \
    --vnet-address-prefix 10.0.0.0/16 \
    --subnet "$SUBNET_NAME" \
    --subnet-address-prefix 10.0.1.0/24 \
    --nsg "$NSG_NAME" \
    --nsg-rule SSH \
    --public-ip-address "$PUBLIC_IP_NAME" \
    --public-ip-sku Standard \
    --storage-sku Premium_LRS \
    --os-disk-size-gb 64 \
    --nic-delete-option Delete \
    --os-disk-delete-option Delete \
    --tags projeto=NivelSeguro disciplina=DevOps \
    --output table

# ---------------- 3. Regra de firewall (NSG) para a API ----------------
# Somente a porta da API e liberada. A 3306 (MySQL) NAO e exposta na internet.
echo ""
echo "[3/4] Liberando a porta $API_PORT (API) no NSG $NSG_NAME"
az network nsg rule create \
    --resource-group "$RESOURCE_GROUP" \
    --nsg-name "$NSG_NAME" \
    --name Allow-API-NivelSeguro \
    --priority 1010 \
    --direction Inbound \
    --access Allow \
    --protocol Tcp \
    --source-address-prefixes "*" \
    --destination-port-ranges "$API_PORT" \
    --output table

# ---------------- 4. Desligamento automatico (economia de creditos) ----------------
echo ""
echo "[4/4] Configurando desligamento automatico diario ($SHUTDOWN_TIME_UTC UTC)"
az vm auto-shutdown \
    --resource-group "$RESOURCE_GROUP" \
    --name "$VM_NAME" \
    --time "$SHUTDOWN_TIME_UTC" \
    --output none \
    || echo "Aviso: nao foi possivel configurar o auto-shutdown (nao impede o uso da VM)."

# ---------------- Resumo ----------------
PUBLIC_IP=$(az vm show \
    --resource-group "$RESOURCE_GROUP" \
    --name "$VM_NAME" \
    --show-details \
    --query publicIps \
    --output tsv)

echo ""
echo "======================================================"
echo " VM CRIADA COM SUCESSO!"
echo "======================================================"
echo " Resource Group : $RESOURCE_GROUP"
echo " VM             : $VM_NAME ($SIZE)"
echo " Regiao         : $LOCATION"
echo " Usuario        : $ADMIN_USERNAME"
echo " IP publico     : $PUBLIC_IP"
echo " Portas abertas : 22 (SSH) e $API_PORT (API)"
echo ""
echo " Proximo passo (instalar Docker na VM):"
echo "   ./scripts/azure/02-configurar-vm.sh"
echo ""
echo " Conexao SSH:"
echo "   ssh $ADMIN_USERNAME@$PUBLIC_IP"
echo "======================================================"
