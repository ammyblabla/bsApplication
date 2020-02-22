echo "Please login before deployment"
RESOURCE_GROUP="ExxonMobil_Sandbox"
az group deployment create \
  --name EventHubDeploy \
  --resource-group ${RESOURCE_GROUP} \
  --template-file "${PWD}/eventHub/template.json"

az group deployment create \
  --name StorageAccountDeploy \
  --resource-group ${RESOURCE_GROUP} \
  --template-file "${PWD}/storageAccount/template.json" \
  --parameters "@${PWD}/storageAccount/parameters.json"


