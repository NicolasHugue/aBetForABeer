terraform {
  required_version = ">= 1.7.0"
  required_providers {
    azurerm = {
      source  = "hashicorp/azurerm"
      version = "~> 4.26.0"
    }
    time = {                              
      source  = "hashicorp/time"
      version = "~> 0.11"
    }
  }
  backend "azurerm" {} # on configure via -backend-config dans le workflow
}

provider "azurerm" {
  features {}
}
