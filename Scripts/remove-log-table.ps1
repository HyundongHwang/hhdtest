[CmdletBinding()]
param(
  [parameter(Mandatory=$true)]
  [string]$TABLE_NAME
)



. "..\Scripts\nnt-ps-include.ps1"



$STORAGE_ACCOUNT_NAME = "hhdandroidtest"
$STORAGE_ACCOUNT_KEY = "zJpcXJNUuvir9ucBO2WuxfDf/bFUfpnATKgNfRI28ByBJrgMzAuiQcY/2ma6udxuvCvnPFWPLf0exIK3n1XbsQ=="
$context = New-AzureStorageContext $STORAGE_ACCOUNT_NAME -StorageAccountKey $STORAGE_ACCOUNT_KEY
Remove-AzureStorageTable -Name $TABLE_NAME -Context $context