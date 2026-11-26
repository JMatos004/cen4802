terraform {
  required_version = ">= 1.13.0"

  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 4.2"
    }
  }
}

variable "docker_host" {
  description = "Docker Engine socket URL"
  type        = string
}

variable "host_port" {
  description = "Local port for the Tennis Score Tracker"
  type        = number
  default     = 8084
}

provider "docker" {
  host = var.docker_host
}

data "docker_image" "tennis" {
  name = "tennis-score-tracker:final-v1"
}

resource "docker_network" "tennis" {
  name = "tennis-iac-network"
}

resource "docker_container" "tennis" {
  name    = "tennis-iac"
  image   = data.docker_image.tennis.id
  restart = "unless-stopped"

  ports {
    internal = 8080
    external = var.host_port
    ip       = "127.0.0.1"
  }

  networks_advanced {
    name = docker_network.tennis.name
  }
}

output "application_url" {
  value = "http://localhost:${var.host_port}"
}
