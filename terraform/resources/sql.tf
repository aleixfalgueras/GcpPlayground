
resource "random_id" "db_name_suffix" {
  byte_length = 4
}

resource "google_sql_database_instance" "sql_mysql_demo" {
  name                = "mysql-demo-${random_id.db_name_suffix.hex}"
  database_version    = "MYSQL_8_0"
  region              = var.region
  deletion_protection = false

  settings {
    tier = "db-f1-micro"
    ip_configuration {
      authorized_networks {
        name  = "allow-all"
        value = "0.0.0.0/0"
      }
    }
  }
}

resource "google_sql_user" "users" {
  name     = "pep_melos"
  instance = google_sql_database_instance.sql_mysql_demo.name
  password = "pepito"
  host     = "%" // any host
}
