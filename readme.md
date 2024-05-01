# Persistent Product Calculator (Kubernetes)

## Table of Contents

- [Container 1](#container-1)
  - [Overview](#overview)
  - [Endpoints](#endpoints)
- [Container 2](#container-2)
  - [Overview](#overview-1)
  - [Endpoints](#endpoints-1)
  - [Additional Notes](#additional-notes-1)
- [Additional Requirements](#additional-requirements)

## Container 1

### Overview

Container 1 serves as a gatekeeper for storing files to a persistent volume in Google Kubernetes Engine (GKE) and calculating products from the stored files. It is deployed as a service in GKE to communicate with the internet and has access to a persistent volume in GKE for storing and retrieving files. Container 1 communicates with Container 2 to calculate product totals and validates input JSON requests.

### Endpoints

- **POST /store-file**

  This endpoint expects Container 1 to create a file and store the data provided in the JSON request to the GKE persistent storage. If successful, it returns a success message. If there's an error storing the file or if invalid JSON input is provided, appropriate error messages are returned.

  **JSON Input:**
  ```json
  {
    "file": "file.dat",
    "data": "product, amount \nwheat, 10\nwheat, 20\noats, 5"
  }
  ```

    **JSON Output (Success):**
    ```json
    {
    "file": "file.dat",
    "message": "Success."
    }
    ```

    **JSON Output (Error - Invalid JSON Input):**
    ```json
    {
    "file": null,
    "error": "Invalid JSON input."
    }
    ``` 

- **POST  /calculate**

    This endpoint allows Container 1 to calculate the total of a product stored in the file. If the file exists and follows the specified CSV format, it returns the total. Otherwise, appropriate error messages are returned.

    **JSON Input:**
    ```json
    {
  "file": "file.dat",
  "product": "wheat"
    }
    ```

    **JSON Output (Success):**

    ```json
    {
  "file": "file.dat",
  "sum": 30
    }
    ```

    **JSON Output (Error - Input File Not in CSV Format):**

    ```json
    {
  "file": "file.dat",
  "error": "Input file not in CSV format."
    }
    ```

## Container 2

### Overview
Container 2 listens on a defined port and endpoint, interacts with Container 1, and calculates the total of a product from the given file stored in the GKE persistent volume.

### Endpoints
- **POST /store-file**: This endpoint receives JSON data from Container 1 for storing files. Container 2 does not process this endpoint but should respond appropriately as per the requirements.

- **POST /calculate**: This endpoint calculates the total of a product from the given file stored in the GKE persistent volume. If successful, it returns the total. If the file is not found or is not in the proper CSV format, appropriate error messages are returned.

### Additional Notes
- Container 2 interacts with Container 1.
- Accesses the GKE persistent volume.
- Calculates product totals from the given file.
- Returns appropriate JSON responses based on success or error conditions.
- Folders container1 and container2 contains source code of the server.
- It also contains the yaml for kubernestes cluster deployment, service, 
persistent volume.
- CloudBuild.yaml file is present in respective container1 and container2 
folder.
- Terraform folder container terraform script to create Google Kubernetes 
Cluster.

## Additional Requirements
1. Create separate repositories for each container in GCP Cloud Source Repository and implement CI/CD pipelines using GCP Cloud Build.
2. Utilize GCP CI/CD tools (Cloud Source Repository, Cloud Build, and Artifact Registry).
3. Utilize Terraform for managing GKE cluster creation.
4. Create a Terraform script to start the GKE cluster and deploy the application workload.
5. Follow specified configurations for GKE cluster creation to optimize cost and resource usage.
6. Attach a persistent volume (1GB) to the GKE cluster and ensure proper access permissions for file operations.
6. Expose Container 1 as a service to the internet and test the functionality before shutting down the cluster.




