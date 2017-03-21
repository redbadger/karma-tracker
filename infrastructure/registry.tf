resource "aws_ecr_repository" "ecr_repositories" {
  name  = "${var.ecr_repositories[count.index]}"
  count = "${length(var.ecr_repositories)}"
}
