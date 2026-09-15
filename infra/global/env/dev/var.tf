variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "us-east-1"
}


variable "domain_name" {
  description = "Domain name for Route 53 hosted zone"
  type        = string
  default = "deployway.online"
}


variable "tags" {
  description = "Tags for Route 53"
  type        = map(string)

  default = {
    Environment = "dev"
    Project     = "global-infrastructure"
    ManagedBy   = "terraform"
    Component   = "route53"
  }
}
