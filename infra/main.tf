# ---------- Resource Group ----------
resource "azurerm_resource_group" "rg" {
  name     = var.resource_group_name
  location = var.location
}

# ---------- ACR ----------
resource "azurerm_container_registry" "acr" {
  name                = var.acr_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  sku                 = "Basic"
  admin_enabled       = true
}

# ---------- User Assigned Identity (pour pull ACR) ----------
resource "azurerm_user_assigned_identity" "uami" {
  name                = "${var.project_name}-uami"
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
}

# Donne à cette identité le droit de pull depuis ACR
resource "azurerm_role_assignment" "acr_pull" {
  scope                = azurerm_container_registry.acr.id
  role_definition_name = "AcrPull"
  principal_id         = azurerm_user_assigned_identity.uami.principal_id
}

# ---------- App Service Plan (Linux) ----------
resource "azurerm_service_plan" "plan" {
  name                = var.service_plan_name
  resource_group_name = azurerm_resource_group.rg.name
  location            = azurerm_resource_group.rg.location
  os_type             = "Linux"
  sku_name            = var.appservice_sku
}

# ---------- PostgreSQL Flexible Server ----------
resource "azurerm_postgresql_flexible_server" "pg" {
  name                   = var.pg_server_name
  resource_group_name    = azurerm_resource_group.rg.name
  location               = var.pg_location
  zone = "1"
  version                = "16"
  administrator_login    = var.pg_admin_login
  administrator_password = var.pg_admin_password

  storage_mb             = var.pg_storage_mb
  sku_name               = var.pg_sku_name

  # accès public (simple); à sécuriser si besoin
  public_network_access_enabled = true
}

resource "time_sleep" "wait_for_pg" {
  depends_on = [azurerm_postgresql_flexible_server.pg]
  create_duration = "120s"  
}

resource "azurerm_postgresql_flexible_server_firewall_rule" "allow_all" {
  name                = "allow-all"
  server_id           = azurerm_postgresql_flexible_server.pg.id
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "255.255.255.255"

   depends_on = [time_sleep.wait_for_pg] 
}

resource "azurerm_postgresql_flexible_server_database" "db" {
  name      = var.pg_db_name
  server_id = azurerm_postgresql_flexible_server.pg.id
  collation = "en_US.utf8"
  charset   = "utf8"
}

# URL JDBC pour Spring (SSL recommandé)
locals {
  jdbc_url = "jdbc:postgresql://${azurerm_postgresql_flexible_server.pg.fqdn}:5432/${var.pg_db_name}?sslmode=require"
}

# ---------- Backend Web App (container) ----------
resource "azurerm_linux_web_app" "backend" {
  name                = var.backend_app_name
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  service_plan_id     = azurerm_service_plan.plan.id

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.uami.id]
  }

  site_config {
    application_stack {
      docker_image_name   = "abfab-backend:${var.image_tag}"
      docker_registry_url = "https://${azurerm_container_registry.acr.login_server}"
    }
    container_registry_use_managed_identity       = true
    container_registry_managed_identity_client_id = azurerm_user_assigned_identity.uami.client_id
  }

  app_settings = {
    WEBSITES_PORT                    = "8080"
    # Spring config via env
    SPRING_PROFILES_ACTIVE        = "prod" 
    SPRING_DATASOURCE_URL            = local.jdbc_url
    SPRING_DATASOURCE_USERNAME       = var.pg_admin_login
    SPRING_DATASOURCE_PASSWORD       = var.pg_admin_password
    APP_JWT_SECRET                   = var.jwt_secret
    APP_JWT_EXP_MINUTES              = "120"
    SPRING_JPA_HIBERNATE_DDL_AUTO    = "validate"
    SPRING_FLYWAY_ENABLED            = "true"
    SPRING_JPA_SHOW_SQL              = "false"
    TZ                               = "UTC"
  }

  https_only = true
}

# ---------- Frontend Web App (container) ----------
resource "azurerm_linux_web_app" "frontend" {
  name                = var.frontend_app_name
  location            = azurerm_resource_group.rg.location
  resource_group_name = azurerm_resource_group.rg.name
  service_plan_id     = azurerm_service_plan.plan.id

  identity {
    type         = "UserAssigned"
    identity_ids = [azurerm_user_assigned_identity.uami.id]
  }

  site_config {
    application_stack {
      docker_image_name   = "abfab-frontend:${var.image_tag}"
      docker_registry_url = "https://${azurerm_container_registry.acr.login_server}"
    }
    container_registry_use_managed_identity       = true
    container_registry_managed_identity_client_id = azurerm_user_assigned_identity.uami.client_id
  }

  app_settings = {
    WEBSITES_PORT       = "80"
    BACKEND_BASE_URL    = "https://${azurerm_linux_web_app.backend.default_hostname}"
    TZ                  = "UTC"
  }

  https_only = true
}