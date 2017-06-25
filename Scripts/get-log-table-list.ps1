[CmdletBinding()]
param(
    [parameter(Mandatory=$false)]
    [string]$FILTER_STR
)



. "..\Scripts\nnt-ps-include.ps1"


# DefaultEndpointsProtocol=https;AccountName=hhdandroidtest;AccountKey=zJpcXJNUuvir9ucBO2WuxfDf/bFUfpnATKgNfRI28ByBJrgMzAuiQcY/2ma6udxuvCvnPFWPLf0exIK3n1XbsQ==;EndpointSuffix=core.windows.net

$STORAGE_ACCOUNT_NAME = "hhdandroidtest"
$STORAGE_ACCOUNT_KEY = "zJpcXJNUuvir9ucBO2WuxfDf/bFUfpnATKgNfRI28ByBJrgMzAuiQcY/2ma6udxuvCvnPFWPLf0exIK3n1XbsQ=="
$context = New-AzureStorageContext $STORAGE_ACCOUNT_NAME -StorageAccountKey $STORAGE_ACCOUNT_KEY
Get-AzureStorageTable -Context $context | where CloudTable -like "*$FILTER_STR*"
