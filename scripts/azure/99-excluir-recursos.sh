#!/bin/bash
# ============================================================
# NivelSeguro - 99) Excluir TODOS os recursos criados no Azure
# ATENCAO: apaga a VM, o disco, a rede e o IP publico.
# Execute somente depois da apresentacao/correcao.
# ============================================================
RESOURCE_GROUP="rg-nivelseguro"

echo "Recursos que serao excluidos:"
az resource list --resource-group "$RESOURCE_GROUP" --output table

read -r -p "Confirma a exclusao do Resource Group $RESOURCE_GROUP? (s/N) " RESPOSTA
if [ "$RESPOSTA" = "s" ] || [ "$RESPOSTA" = "S" ]; then
    az group delete --name "$RESOURCE_GROUP" --yes --no-wait
    echo "Exclusao iniciada. Acompanhe com: az group list --output table"
else
    echo "Operacao cancelada."
fi
