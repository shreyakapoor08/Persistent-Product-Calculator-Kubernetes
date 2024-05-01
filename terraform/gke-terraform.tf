provider "google" {
  project     = "shreya-kubernetes"
  region      = "us-central1"

}

# GKE Cluster
resource "google_container_cluster" "gke_cluster" {
  name     = "kubernetes-cluster"
  location = "us-central1-c"

  initial_node_count = 1

  node_config {
    machine_type = "e2-micro"
    disk_size_gb = 10
  }
  deletion_protection = false

}

