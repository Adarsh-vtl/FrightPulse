# FreightPulse

Dispatch load balancer backend that assigns delivery orders to a fleet of vehicles based on vehicle capacity, order priority, and geographical distance.

## How the Algorithm Works

1. Orders are sorted by priority (`HIGH`, `MEDIUM`, `LOW`). Within the same priority, heavier packages are evaluated first.
2. For each order, vehicles with enough remaining capacity are filtered.
3. The Haversine formula calculates the distance between the order and each candidate vehicle's current position (its starting location or the location of its latest assigned drop).
4. The nearest vehicle is selected, the order is assigned, the package weight is added to its load, and the vehicle position is updated to the order location.
5. If no vehicle can accommodate an order, it is added to `unassignedOrders` with a descriptive reason.
6. Submitting new orders or vehicles replaces previous data in memory. The dispatch plan is generated dynamically without mutating the original input objects.

## Tech Stack

- Java 17
- Spring Boot 3.2.5
- Maven
- springdoc-openapi (Swagger UI)
- JUnit 5, Mockito, MockMvc
- In-memory data structures

## Running Locally

Prerequisites: JDK 17+ and Maven.

1. Clone the repository and navigate to the project directory:
   ```bash
   git clone https://github.com/Adarsh-vtl/FrightPulse.git
   cd FrightPulse
   ```

2. Build the project:
   ```bash
   mvn clean package
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application starts on port `8080` by default. You can change the port with the `PORT` environment variable:
```bash
PORT=9090 mvn spring-boot:run
```

## Running Tests

Run the full test suite with Maven:
```bash
mvn test
```

## Swagger Documentation

Swagger UI is available at:
```
/swagger-ui/index.html
```

Visiting the root path `/` automatically redirects to the Swagger UI page.

## API Examples

Note: The examples below use `http://localhost:8080`. Replace this with your deployed server URL when running against a remote environment.

### 1. Submit Delivery Orders

```bash
curl -X POST http://localhost:8080/api/dispatch/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orders": [
      {
        "orderId": "ORD001",
        "latitude": 12.9716,
        "longitude": 77.5946,
        "address": "MG Road, Bangalore, Karnataka, India",
        "packageWeight": 10,
        "priority": "HIGH"
      }
    ]
  }'
```

Response:
```json
{
  "message": "Delivery orders accepted.",
  "status": "success"
}
```

### 2. Submit Fleet Vehicles

```bash
curl -X POST http://localhost:8080/api/dispatch/vehicles \
  -H "Content-Type: application/json" \
  -d '{
    "vehicles": [
      {
        "vehicleId": "VEH001",
        "capacity": 100,
        "currentLatitude": 12.9716,
        "currentLongitude": 77.6413,
        "currentAddress": "Indiranagar, Bangalore, Karnataka, India"
      }
    ]
  }'
```

Response:
```json
{
  "message": "Vehicle details accepted.",
  "status": "success"
}
```

### 3. Generate Dispatch Plan

```bash
curl -X GET http://localhost:8080/api/dispatch/plan
```

Response:
```json
{
  "dispatchPlan": [
    {
      "vehicleId": "VEH001",
      "totalLoad": 10,
      "totalDistance": "5.19 km",
      "assignedOrders": [
        {
          "orderId": "ORD001",
          "latitude": 12.9716,
          "longitude": 77.5946,
          "address": "MG Road, Bangalore, Karnataka, India",
          "packageWeight": 10,
          "priority": "HIGH"
        }
      ]
    }
  ],
  "unassignedOrders": []
}
```

## Deploying to Render

1. Create a new Web Service on Render and connect your GitHub repository.
2. Select **Docker** as the environment (Render detects the root `Dockerfile`).
3. Render automatically sets the `PORT` environment variable. The app binds to `${PORT:8080}` as configured.
4. Note: On the Render free tier, web services spin down after 15 minutes of inactivity. The initial request after idle may take 30 to 50 seconds to boot up.
