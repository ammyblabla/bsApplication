echo "Please login before deployment"
az login
RESOURCE_GROUP="ExxonMobil_Sandbox"
DIRECTORY=$(dirname $(realpath $0))
echo "Create Event Hub"
az group deployment create \
  --name EventHubDeploy \
  --resource-group ${RESOURCE_GROUP} \
  --template-file "${DIRECTORY}/eventHub/template.json"

echo "Create Storage Account"
az group deployment create \
  --name StorageAccountDeploy \
  --resource-group ${RESOURCE_GROUP} \
  --template-file "${DIRECTORY}/storageAccount/template.json"

