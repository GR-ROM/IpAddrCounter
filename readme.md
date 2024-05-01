# Unique IP Address Counter

This project is a simple Java application that counts the number of unique IP addresses in a file.

## Getting Started

To get started with this project, follow these steps:

1. Clone the repository:

    ```bash
    git clone <repository_url>
    ```

2. Navigate to the project directory:

    ```bash
    cd unique-ip-counter
    ```

3. Compile the Java code:

    ```bash
    gradle build jar
    ```

4. Run the application:

    ```bash
    java -jar IpAddrCounter-0.0.1-SNAPSHOT.jar <filename> [threads]
    ```

   Replace `<filename>` with the path to the file containing IP addresses and optional `[threads]` with the number of worker threads default value is 4.

## Usage

This application takes a text file containing IP addresses as input and prints the number of unique IP addresses to the console.

Example usage:

```bash
java -jar IpAddrCounter-0.0.1-SNAPSHOT.jar input.txt 16 