# Homomorphic Encryption Payroll System

This project demonstrates the concept of **homomorphic encryption** applied to payroll systems. The system allows for
the storage and manipulation of employee payroll data in an encrypted form, without needing to decrypt it for
processing. It achieves this through **partial homomorphic encryption**.

### Key Features:

- **Homomorphic Encryption**: We use homomorphic encryption to ensure that payroll data (such as gross salary, net
  salary, tax, and deductions) is stored securely in the database. With homomorphic encryption, mathematical operations
  can be performed on encrypted data without revealing the plaintext.
- **Partial Homomorphic Encryption**: This project specifically implements **partial homomorphic encryption**, which
  supports certain types of operations (like addition and multiplication) on encrypted data, ensuring that we can
  calculate totals without needing to decrypt the information.
- **One-to-Many Relationship**: The system uses a one-to-many relationship where each employee can have multiple payroll
  records.

### APIs:

1. **Create Employee**:
    - Endpoint: `POST /api/employees`
    - Description: Creates a new employee record in the system.

2. **Add Payroll**:
    - Endpoint: `POST /api/payrolls`
    - Description: Adds a payroll record for an employee with encrypted salary details.

3. **Calculate Total Net Pay for Employee**:
    - Endpoint: `GET /api/employees/total-payroll`
    - Description: Calculates the total net pay for an employee over all payrolls without decrypting the data in the
      database. The calculation is performed on encrypted data.

### Database Schema:

- The database has a **one-to-many relationship** where each employee can have multiple payroll records.
- Payroll information is stored **encrypted** in the database.

### Libraries Used:

- **Paillier Encryption Library**: We used the [Paillier library](https://github.com/n1analytics/paillier) for
  encryption, which allows us to perform operations on encrypted numbers.

### Custom Serializer and Deserializer

The Paillier encryption library does not provide built-in serialization or deserialization methods for `EncryptedNumber`
objects, so we had to implement custom mechanisms for storing and retrieving encrypted numbers in the database.

#### Serializer:

- **Purpose**: The custom **Serializer** allows us to convert the `EncryptedNumber` object into a format that can be
  stored in the database (in this case, as a `String`).
- **How it works**:
    - When we want to save an encrypted value, we convert the `EncryptedNumber` object to a string representation using
      the `toString()` method (which is called in the payroll creation process). This string represents the encrypted
      number, and it's stored in the database.
    - The `EncryptedNumber` itself contains a ciphertext (encrypted data), an exponent, and a context (encryption
      settings). The serializer ensures that these parts are correctly serialized into a storable format.

#### Deserializer:

- **Purpose**: The **Deserializer** is used to convert the stored string representation of an `EncryptedNumber` back
  into the actual `EncryptedNumber` object when retrieving data from the database.
- **How it works**:
    - Once we retrieve the encrypted payroll data from the database, we need to reconstruct the `EncryptedNumber` object
      to perform operations on it. The deserializer extracts the encrypted data from the string format, and using the
      same encryption context, it reassembles the object with the appropriate ciphertext, exponent, and context for
      further processing.

#### Example:

To handle the `EncryptedNumber` in a more straightforward way, the `toString()` method is used to store the object as a
`String` in the database. When reading back from the database, we convert this string back into an `EncryptedNumber` by
using the custom deserialization logic.

Here's how it fits into the code:

1. **Serializer**: Used when saving the encrypted payroll data into the database.
   ```java
   public void serialize(Serializer serializer) {
       serializer.serialize(this.context, this.calculateCiphertext(), this.exponent);
   }
    ```

2. **Deserializer**: Used when reading the encrypted payroll data from the database.
    ```java
    public EncryptedNumber deserialize(String encryptedString) {
        return new EncryptedNumber(this.context, encryptedString);
    }
    ```

### GMP Library Requirement:

- The Paillier library requires the **GMP.dll** (GNU Multiple Precision Arithmetic Library) to perform optimized
  cryptographic operations.
- **Installation Steps**:
    1. Download the `gmp.dll` file from [GMP official site](https://gmplib.org/).
    2. Place the `gmp.dll` file in the `lib` folder of the project.
    3. Add the library path to your system environment variables.
    4. Use the following command to install the library (for Windows):
       ```bash
       set PATH=%PATH%;C:\path\to\gmp.dll
       ```
    5. Restart your IDE or terminal.
    6. To install the library on Linux, use the following command:
         ```bash
         sudo apt-get install libgmp3-dev
         ```
    7. To install the library on macOS, use the following command:
       ```bash
         brew install gmp
         ```

### Global Exception Handling:

- The system has global exception handling to ensure that all errors are captured and meaningful messages are returned
  to the client. This helps to provide a more user-friendly and robust API.

### Test Coverage:

- We used **JaCoCo** for test coverage analysis, and the system has **100% code coverage**. All core functionalities
  have been thoroughly tested to ensure correctness and reliability.

---

### Project Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/ramimohsen/homomorphic-encryption.git
   cd homomorphic-encryption
   ```

2. **Install Dependencies**:
   ```bash
    mvn clean install
    ```

3. **Run the Application**:
    ```bash
    mvn spring-boot:run
    ```

### Technologies Used:

- **Java 21**
- **Spring Boot 3.4**
- **Paillier Encryption Library**
- **H2 Database**
- **Spring Data JPA**
- **Swagger**

### Swagger UI:

The Swagger UI can be accessed at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

