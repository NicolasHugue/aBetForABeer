output "acr_login_server" {
  value       = azurerm_container_registry.acr.login_server
  description = "Adresse du registre ACR"
}

output "backend_url" {
  value       = "https://${azurerm_linux_web_app.backend.default_hostname}"
  description = "URL backend"
}

output "frontend_url" {
  value       = "https://${azurerm_linux_web_app.frontend.default_hostname}"
  description = "URL frontend"
}
