# Assignment 10: Infrastructure as Code

This implementation reuses my Tennis Score Tracker Java application and Dockerfile from earlier CEN4802 assignments. Terraform configuration and documentation were developed with assistance from ChatGPT.

## Environment and resources

Tested with Terraform 1.13.5, kreuzwerker/docker provider 4.7.0, and Docker Desktop on macOS Apple Silicon.

Terraform manages:
- A dedicated Docker bridge network: tennis-iac-network.
- An application container: tennis-iac, with restart policy unless-stopped.
- A localhost port mapping to the application's internal port 8080.

The existing tennis-score-tracker:monitoring image is read through a data source. Terraform does not manage or remove that image.

## Prerequisites

Install Terraform >= 1.13, Java 17 or a compatible newer JDK, Maven, and Docker. Start Docker Desktop.

From the repository root, build the application and local image:

```bash
mvn clean package
docker build -t tennis-score-tracker:monitoring .
```

## Initialize, validate, and provision

From the repository root:

```bash
cd infra/terraform
export TF_VAR_docker_host="$(docker context inspect --format '{{.Endpoints.docker.Host}}')"
terraform init
terraform fmt -check
terraform validate
terraform plan -var="host_port=8084" -out=initial.tfplan
terraform apply initial.tfplan
docker ps --filter "name=^/tennis-iac$"
docker network inspect tennis-iac-network
terraform state list
```

Open http://localhost:8084 and verify the Tennis Score Tracker loads.

## Modify

The initial deployment used host port 8084. I changed terraform.tfvars to host_port = 8085. The committed file contains this final value.

```bash
terraform validate
terraform plan -out=modified.tfplan
terraform apply modified.tfplan
docker ps --filter "name=^/tennis-iac$"
docker inspect tennis-iac
terraform plan
```

Open http://localhost:8085. The observed plan proposed one resource addition and one destruction because changing the published port forced container replacement. Application verification succeeded at the new port. The network was retained.

## Remove and verify

Run from infra/terraform:

```bash
terraform destroy
docker ps -a --filter "name=^/tennis-iac$"
docker network ls --filter "name=^tennis-iac-network$"
terraform state list
```

Enter yes at the destruction prompt. The completed execution reported two resources destroyed. Both Docker listings were empty except for headings, and Terraform state list returned no entries.

## Results and limitations

Initial provisioning created the network before its dependent container. The saved execution plan made the proposed changes reviewable before application. Configuration replaced manual network and container creation commands with version-controlled desired settings.

The port change required container replacement and briefly interrupted service. This implementation depends on a locally built image and a running Docker Engine. It does not provide persistent application storage or high availability. Future improvements could include immutable image references and automated application health checks.

The Docker socket is supplied through TF_VAR_docker_host to avoid hardcoding a user's filesystem path. Re-export this variable in each new Terminal session. Provider downloads, Terraform state, and saved plans are excluded from Git; the provider lock file is included.

## References

- My existing CEN4802 Tennis Score Tracker application and Docker configuration.
- HashiCorp Terraform Docker tutorials: https://developer.hashicorp.com/terraform/tutorials/docker-get-started
- Docker provider documentation: https://registry.terraform.io/providers/kreuzwerker/docker/latest/docs
- ChatGPT assistance with Terraform configuration, commands, and documentation.
