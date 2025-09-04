variable "location" {
  type        = string
  default     = "westeurope"
}

variable "project_name" {
  type        = string
  default     = "abfab"
}

variable "resource_group_name" {
  type        = string
  default     = "rg-abfab"
}

variable "acr_name" {
  type        = string
  default     = "abfabacr1234"
}

variable "service_plan_name" {
  type        = string
  default     = "abfab-plan"
}

variable "backend_app_name" {
  type        = string
  default     = "abfab-backend"
}

variable "frontend_app_name" {
  type        = string
  default     = "abfab-frontend"
}

variable "pg_server_name" {
  type        = string
  default     = "abfab-pg-flex"
}

variable "pg_admin_login" {
  type        = string
  default     = "pgadmin"
}

variable "pg_admin_password" {
  type        = string
  sensitive   = true
}

variable "pg_db_name" {
  type        = string
  default     = "abetforabeer"
}

variable "image_tag" {
  type        = string
  default     = "latest"
}

variable "jwt_secret" {
  type        = string
  sensitive   = true
}

variable "appservice_sku" { 
  type = string  
  default = "B1" 
} 
variable "pg_sku_name" { 
  type = string 
  default = "B_Standard_B1ms" 
}
variable "pg_storage_mb" { 
  type = number  
  default = 32768 
} 

variable "pg_location" {
  type    = string
  default = "francecentral"
}