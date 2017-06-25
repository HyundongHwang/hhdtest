sal open "C:\Windows\SysWOW64\explorer.exe"



ls "$PSScriptRoot\*.properties.ps1" | foreach {
    . $_.FullName
}



<#
.SYNOPSIS
.EXAMPLE
#>
function hhd-module-install-import
{
    [CmdletBinding()]
    param
    (
        [Parameter(Mandatory=$true, ValueFromPipeline=$true, ValueFromPipelinebyPropertyName=$true)]
        [System.String]
        $MODULE_NAME
    )

    if((Get-InstalledModule -name $MODULE_NAME).count -eq 0)
    {
        write "install $MODULE_NAME ..."
        Install-Package $MODULE_NAME -Force -AllowClobber
    }
    else 
    {
        write "$MODULE_NAME already installed !!!"
    }

    write "import $MODULE_NAME ..."
    Import-Module $MODULE_NAME -Force
}



function hhd-azure-log-get-table-list
{
    [CmdletBinding()]
    param(
        [parameter(Mandatory=$false)]
        [string]$FILTER_STR
    )



    $context = New-AzureStorageContext $STORAGE_ACCOUNT_NAME -StorageAccountKey $STORAGE_ACCOUNT_KEY
    Get-AzureStorageTable -Context $context | where CloudTable -like "*$FILTER_STR*"
}




function hhd-azure-log-get-log
{
    [CmdletBinding()]
    param
    (
        [Parameter(Mandatory=$true, ValueFromPipeline=$true, ValueFromPipelinebyPropertyName=$true)]
        [System.String]
        $TABLE_NAME,
  
        [Parameter(Mandatory=$true, ValueFromPipeline=$true, ValueFromPipelinebyPropertyName=$true)]
        [ValidateSet("ERROR","WARNI","INFOR","DEBUG","VERBO")]
        [System.String]
        $LOG_LEVEL,

        [Parameter(Mandatory=$true, ValueFromPipeline=$true, ValueFromPipelinebyPropertyName=$true)]
        [switch]
        $KEEP_MONITOR = $false
    )



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

    if (!$KEEP_MONITOR) 
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
}



function hhd-azure-log-get-session
{
    [CmdletBinding()]
    param
    (
        [Parameter(Mandatory=$true, ValueFromPipeline=$true, ValueFromPipelinebyPropertyName=$true)]
        [System.String]
        $FILTER_STR,

        [Parameter(Mandatory=$true, ValueFromPipeline=$true, ValueFromPipelinebyPropertyName=$true)]
        [System.String]
        $FILTER_STR2
    )



    $context = New-AzureStorageContext $STORAGE_ACCOUNT_NAME -StorageAccountKey $STORAGE_ACCOUNT_KEY
    $table = Get-AzureStorageTable -Context $context -Name "session"
    $query = New-Object Microsoft.WindowsAzure.Storage.Table.TableQuery
    $selectColums = New-Object System.Collections.Generic.List[string]
    $selectColums.Add("RowKey")
    $selectColums.Add("SessionStr")
    $query.SelectColumns = $selectColums

    Write-Verbose "query.FilterString : $($query.FilterString)"
    $entities = $table.CloudTable.ExecuteQuery($query) | sort RowKey



    $entities | % { 

        $rowKey = $_.RowKey
        $sessionStr = $_.Properties["SessionStr"].StringValue

        if (($rowKey -notlike "*$FILTER_STR*") -and 
            ($sessionStr -notlike "*$FILTER_STR*"))
        {
            return $null
        }

        if($FILTER_STR2)
        {
            $filteredStr = $sessionStr -split "\n" | sls $FILTER_STR2
        }

        $obj = New-Object -typename PSObject
        $obj | Add-Member -MemberType NoteProperty -Name RowKey -Value $rowKey
        $obj | Add-Member -MemberType NoteProperty -Name SessionStr -Value $sessionStr
        $obj | Add-Member -MemberType NoteProperty -Name FilteredStr -Value $filteredStr
        return $obj
    }
}



function hhd-azure-log-remove-table
{
    [CmdletBinding()]
    param
    (
        [Parameter(Mandatory=$true, ValueFromPipeline=$true, ValueFromPipelinebyPropertyName=$true)]
        [System.String]
        $TABLE_NAME
    )



    $context = New-AzureStorageContext $STORAGE_ACCOUNT_NAME -StorageAccountKey $STORAGE_ACCOUNT_KEY
    Remove-AzureStorageTable -Name $TABLE_NAME -Context $context
}