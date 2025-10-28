$services = @(
    @{
        Name = "Eureka Service"
        Directory = "eureka-service"
        Command = "mvn spring-boot:run"
        Delay = 0  # Start immediately
    },
    @{
        Name = "Account Service"
        Directory = "account-service"
        Command = "mvn clean spring-boot:run"
        Delay = 5  # Wait 10 seconds after previous (e.g., for Eureka to start)
    },
    @{
        Name = "Product Service"
        Directory = "product-service"
        Command = "mvn clean spring-boot:run"
        Delay = 5  # Wait 10 seconds after previous (e.g., for Eureka to start)
    },
    @{
        Name = "Sales Service"
        Directory = "sales-service"
        Command = "mvn clean spring-boot:run"
        Delay = 5
    },
    @{
        Name = "Gateway Service"
        Directory = "gateway-service"
        Command = "mvn clean spring-boot:run"  # For JARs
        Delay = 5
    }
)

foreach ($service in $services) {
    if ($service.Delay -gt 0) {
        Write-Host "Waiting $($service.Delay) seconds for $($service.Name)..." -ForegroundColor Cyan
        Start-Sleep -Seconds $service.Delay
    }

    $startProcessArgs = @{
        FilePath = "powershell.exe"
        ArgumentList = "-NoExit", "-Command", "cd '$PWD\$($service.Directory)'; $($service.Command)"
        WorkingDirectory = "$PWD\$($service.Directory)"
    }

    Start-Process @startProcessArgs
    Write-Host "Started $($service.Name) in new window." -ForegroundColor Green
}

Write-Host "All services launched! Press any key to exit this script." -ForegroundColor Green
$null = $Host.UI.RawUI.ReadKey("NoEcho,IncludeKeyDown")