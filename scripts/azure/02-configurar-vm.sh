#!/bin/bash
# ============================================================
# NivelSeguro - 02) Configurar a VM: ferramentas, swap e Docker
#
# Onde executar: Azure Cloud Shell (Bash), depois do script 01.
# Como executar:
#   chmod +x scripts/azure/02-configurar-vm.sh
#   ./scripts/azure/02-configurar-vm.sh
#
# Usa "az vm run-command invoke" para rodar os comandos DENTRO da VM
# sem precisar de SSH. Cada etapa pode levar alguns minutos.
# ============================================================
set -e

RESOURCE_GROUP="rg-nivelseguro"
VM_NAME="vm-nivelseguro"
ADMIN_USERNAME="admlnx"

# Executa um script na VM e mostra apenas a saida (stdout/stderr)
executar_na_vm() {
    az vm run-command invoke \
        --resource-group "$RESOURCE_GROUP" \
        --name "$VM_NAME" \
        --command-id RunShellScript \
        --scripts "$1" \
        --query "value[0].message" \
        --output tsv
}

echo "======================================================"
echo " NivelSeguro - configurando a VM $VM_NAME"
echo "======================================================"

# ---------------- 1. Memoria swap ----------------
# A VM Standard_B2ats_v2 tem 1 GB de RAM. O build da imagem Java (Maven)
# e o MySQL juntos podem passar disso; 2 GB de swap evitam falta de memoria.
# O swap e criado primeiro porque o "dnf update" tambem consome bastante memoria.
echo ""
echo "[1/4] Criando 2 GB de memoria swap..."
executar_na_vm "
    if [ ! -f /swapfile ]; then
        dd if=/dev/zero of=/swapfile bs=1M count=2048 status=none
        chmod 600 /swapfile
        mkswap /swapfile
        swapon /swapfile
        echo '/swapfile none swap defaults 0 0' >> /etc/fstab
    fi
    free -m
"

# ---------------- 2. Pacotes basicos ----------------
echo ""
echo "[2/4] Atualizando o sistema e instalando git, nano, tree e yum-utils..."
executar_na_vm "
    dnf -y update
    dnf -y install git nano tree yum-utils
"

# ---------------- 3. Docker Engine + Docker Compose ----------------
echo ""
echo "[3/4] Instalando Docker Engine e o plugin Docker Compose..."
executar_na_vm "
    dnf -y remove podman buildah runc || true
    yum-config-manager --add-repo https://download.docker.com/linux/rhel/docker-ce.repo
    dnf -y install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
"

# ---------------- 4. Configurar Docker ----------------
echo ""
echo "[4/4] Iniciando o Docker e liberando o usuario $ADMIN_USERNAME no grupo docker..."
executar_na_vm "
    systemctl enable --now docker
    systemctl enable containerd
    usermod -aG docker $ADMIN_USERNAME
    docker --version
    docker compose version
    systemctl is-active docker
"

PUBLIC_IP=$(az vm show \
    --resource-group "$RESOURCE_GROUP" \
    --name "$VM_NAME" \
    --show-details \
    --query publicIps \
    --output tsv)

echo ""
echo "======================================================"
echo " VM CONFIGURADA COM SUCESSO!"
echo "======================================================"
echo " Instalados: git, nano, tree, Docker Engine, Docker Compose"
echo " Swap: 2 GB"
echo ""
echo " Conecte na VM:"
echo "   ssh $ADMIN_USERNAME@$PUBLIC_IP"
echo ""
echo " ATENCAO: no primeiro acesso execute:"
echo "   newgrp docker"
echo "======================================================"
