﻿<#
.Synopsis
   Synchronisieren der Lehrer mit der AD
.DESCRIPTION
   Synchronisieren der Lehrer mit der AD
.EXAMPLE
   Sync-Teachers
#>
function Sync-Teachers
{
    Param
    (
         [switch]$whatif,
        [switch]$force
    )

    Begin
    {
        $ret="" | Select-Object "new","update","delete","error","msg"
        $ret.new=0;
        $ret.update=0;
        $ret.delete=0;
        $ret.error=0;
        $ret.msg="";
        $config=Get-Content "$PSScriptRoot/config.json" | ConvertFrom-json
        $password = $config.bindpassword | ConvertTo-SecureString -asPlainText -Force
        $credentials = New-Object System.Management.Automation.PSCredential -ArgumentList "ldap-user", $password
        $u=Get-ADGroupMember -Identity Lehrer -Server 172.31.0.1 -Credential $credentials | Get-ADUser -Properties Mail,Initials -Server 172.31.0.1 -Credential $credentials
        foreach ($user in $u) {
            if ($user.Initials) {
                $ret.msg+= "Bearbeite $($user.GivenName) $($user.Name) ($($user.Initials))`r`n" 
                Write-Host  "Bearbeite $($user.GivenName) $($user.Name) ($($user.Initials))" 
               
                $t=Get-Teacher -ID $user.Initials
                if ($t) {
                    Write-Host "Lehrer gefunden aktualisiere Daten" -BackgroundColor DarkGreen
                    $ret.msg+= "+- Lehrer gefunden aktualisiere Daten`r`n" 
                    $ret.update++;
                    if (-not $whatif) {
                        $r=Set-Teacher -ID $user.Initials -VNAME $user.GivenName -NNAME $user.Name -EMAIL $user.Mail
                    }
                }
                else {
                    Write-Host "Neuer Lehrer, wird angelegt!" -BackgroundColor DarkRed
                    $ret.msg+= "+- Neuer Lehrer, wird angelegt!`r`n" 
                    $ret.new++;
                    if (-not $force) {
                        $r=Read-Host "Neuen Lehrer anlegen (j/n)"
                        if ($r -eq "j") {
                           if (-not $whatif) {
                                $r=New-Teacher -ID $user.Initials -VNAME $user.GivenName -NNAME $user.Name -EMAIL $user.Mail
                            }
                        }
                    }
                    if (-not $whatif) {
                        $r=New-Teacher -ID $user.Initials -VNAME $user.GivenName -NNAME $user.Name -EMAIL $user.Mail
                    }
                }
            }
            else {
                Write-Host "Achtung der Lehrer "$user.GivenName" "$user.Name" hat keine Initialen (Kürzel) und kann nicht synchronisiert werden" -BackgroundColor DarkRed
                $ret.msg+= "#Achtung der Lehrer $($user.GivenName) $($user.Name) hat keine Initialen (Kürzel) und kann nicht synchronisiert werden`r`n" 
                $ret.error++;
            }    
        }
        <#
        $teachers = Get-Teachers
        foreach ($t in $teachers) {
            $found=$false;
            foreach ($user in $u) {
                if ($t.id -eq $user.Initials) {
                    $found=$true;
                    break;
                }
            }
            if (-not $found) {
                Write-Host "Achtung der Lehrer mit Initialen "$t.ID" ("$t.VNAME" "$t.NNAME") wurde nicht gefunden" -BackgroundColor DarkRed
                $ret.msg+= "Achtung der Lehrer mit Initialen $($t.ID) ($($t.VNAME) $($t.NNAME)) wurde nicht gefunden`r`n" 
                $ret.delete++;
                if (-not $force) {
                    $r=Read-Host "Soll der Lehrer aus dem Klassenbuch entfernt werden? (j/n)"
                    if ($r -eq "j") {
                        if (-not $whatif) {
                            $r=Delete-Teacher -ID $t.ID
                        }
                        Write-Host "Lehrer "$t.ID" gelöscht!" -BackgroundColor DarkGreen
                        $ret.msg+= "+- Lehrer $($t.ID) gelöscht!`r`n" 
                    }
                }
                else {
                    if (-not $whatif) {
                        $r=Delete-Teacher -ID $t.ID
                    }
                    Write-Host "Lehrer "$t.ID" gelöscht!" -BackgroundColor DarkGreen
                    $ret.msg+= "+- Lehrer $($t.ID) gelöscht!`r`n" 
                }
            }
        }
        #>
        return $ret
    }
}
