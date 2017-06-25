[CmdletBinding()]
param(
    [parameter(Mandatory=$false)]
    [string]$FILTER_STR,

    [parameter(Mandatory=$false)]
    [string]$FILTER_STR2
)



. "..\Scripts\nnt-ps-include.ps1"




$STORAGE_ACCOUNT_NAME = "nntcoconut"
$STORAGE_ACCOUNT_KEY = "3AXKHnqMQ3WvNowW7zt2iAunuEeRJEdczc7mXyatVw4AW8/USS88FBEjrKTBZPyRIMpaa+xBbA963qKnPFZ6zw=="
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