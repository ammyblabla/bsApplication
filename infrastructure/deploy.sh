echo "Please login before deployment"
RESOURCE_GROUP="ExxonMobil_Sandbox"
DIRECTORY=$(dirname $(realpath $0))
az group deployment create \
  --name EventHubDeploy \
  --resource-group ${RESOURCE_GROUP} \
  --template-file "${DIRECTORY}/eventHub/template.json"

az group deployment create \
  --name StorageAccountDeploy \
  --resource-group ${RESOURCE_GROUP} \
  --template-file "${DIRECTORY}/storageAccount/template.json"

