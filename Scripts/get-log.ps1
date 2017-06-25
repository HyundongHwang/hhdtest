[CmdletBinding()]
param(
    [parameter(Mandatory=$true)]
    [string]$TABLE_NAME,
  
    [parameter(Mandatory=$false)]
    [ValidateSet("ERROR","WARNI","INFOR","DEBUG","VERBO")]
    [string]$LOG_LEVEL,

    [parameter(Mandatory = $false)]
    [switch]$keepMonitor = $false
)



. "..\Scripts\nnt-ps-include.ps1"



$STORAGE_ACCOUNT_NAME = "hhdandroidtest"
$STORAGE_ACCOUNT_KEY = "zJpcXJNUuvir9ucBO2WuxfDf/bFUfpnATKgNfRI28ByBJrgMzAuiQcY/2ma6udxuvCvnPFWPLf0exIK3n1XbsQ=="
$context = New-AzureStorageContext $STORAGE_ACCOUNT_NAME -StorageAccountKey $STORAGE_ACCOUNT_KEY
$table = Get-AzureStorageTable -Context $context -Name $TABLE_NAME
$query = New-Object Microsoft.WindowsAzure.Storage.Table.TableQuery
$selectColums = New-Object System.Collections.Generic.List[string]
$selectColums.Add("RowKey")
$selectColums.Add("PartitionKey")
$selectColums.Add("Timestamp")
$selectColums.Add("Log")
$selectColums.Add("LogLevel")
$query.SelectColumns = $selectColums
[System.String]$logLevelQuery

if([System.String]::IsNullOrWhiteSpace($LOG_LEVEL) -eq $false)
{
    $logLevelQuery = "PartitionKey eq '{0}'" -f $LOG_LEVEL
    $query.FilterString = $logLevelQuery
}

Write-Verbose ("query.FilterString : {0}" -f $query.FilterString)
$entities = $table.CloudTable.ExecuteQuery($query) | sort RowKey
#$entities | select RowKey, PartitionKey, TimeStamp, @{l="Log"; e = {$_.Properties["Log"].StringValue}} | fl
$lastRowKey = $null

$entities | %{ 
    # write ("[{0}][{1}][{2}]{3}" -f $_.RowKey, $_.Properties["LogLevel"].StringValue, $_.TimeStamp.ToLocalTime().ToString("HH:mm:ss"), $_.Properties["Log"].StringValue)
    $rowKey = $_.RowKey
    $rowLogLevel = $_.Properties["LogLevel"].StringValue
    $timeStr = $_.TimeStamp.ToLocalTime().ToString("HH:mm:ss")
    $log = $_.Properties["Log"].StringValue

    write "[$rowKey][$rowLogLevel][$timeStr]$log"
    $lastRowKey = $_.RowKey 
}

if (!$keepMonitor) 
{
    return
}



while($true)
{
    $nextQuery = "RowKey gt '{0}'" -f $lastRowKey

    if([System.String]::IsNullOrWhiteSpace($logLevelQuery) -eq $false)
    {
        $query.FilterString = "({0}) and ({1})" -f $logLevelQuery, $nextQuery
    }
    else
    {
        $query.FilterString = $nextQuery
    }

    Write-Verbose ("query.FilterString : {0}" -f $query.FilterString)
    $entities = $table.CloudTable.ExecuteQuery($query) | sort RowKey

    $entities | %{ 
        write ("[{0}][{1}][{2}] {3}" -f $_.RowKey, $_.Properties["LogLevel"].StringValue, $_.TimeStamp.ToLocalTime().ToString("HH:mm:ss"), $_.Properties["Log"].StringValue)
        $lastRowKey = $_.RowKey
    }

    [System.Threading.Thread]::Sleep(3000)
    write "wait ..."
}