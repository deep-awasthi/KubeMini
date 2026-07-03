# KubeMini

KubeMini is a lightweight Kubernetes-inspired container orchestration backend written in Java 21 and Spring Boot 3. It keeps desired state separate from actual state, schedules pods onto registered worker nodes, reconciles deployments into running containers, persists cluster events, and exposes REST plus WebSocket APIs.

## Architecture

- `common`: API envelopes, error model, shared event type, ID helpers.
- `domain`: pure orchestration model for nodes, deployments, pods, services, resources, probes, autoscaling, and cluster snapshots.
- `application`: use cases, repository/runtime/event ports, scheduling strategies, reconciliation loops, health/autoscaling controllers.
- `infrastructure`: Spring Data JPA adapters, Postgres persistence, Docker Java runtime adapter, simulated runtime, Micrometer metrics, persisted event publishing.
- `api`: Spring Boot application, DTOs, REST controllers, JWT/RBAC, OpenAPI, WebSocket event stream.

## Core Capabilities

- Node registration, deletion, drain, cordon, heartbeat-ready domain behavior.
- Deployment create/update/delete/scale/pause/resume/rollback with version history.
- Replica reconciliation from desired replicas to running pods.
- Schedulers: least-loaded, round-robin, bin-packing, random, and resource-aware aliases.
- Resource-aware scheduling across CPU, memory, and disk requests.
- Simulated ClusterIP/NodePort/LoadBalancer service records and virtual IP service discovery.
- Persisted cluster events for actions such as `NodeJoined`, `DeploymentCreated`, `PodCreated`, `DeploymentScaled`, and rollback/update events.
- JWT authentication with ADMIN, OPERATOR, DEVELOPER, and READ_ONLY role checks.
- OpenAPI UI at `/swagger-ui.html`, Prometheus metrics at `/actuator/prometheus`, and WebSocket events at `/ws/events` on `/topic/cluster-events`.

## Run Locally

Start Postgres, Prometheus, Grafana, and KubeMini:

```bash
docker compose up --build
```

By default, `KUBEMINI_RUNTIME_MODE=simulated`, so deployments reconcile without starting real Docker containers. To run against Docker Engine, set:

```bash
KUBEMINI_RUNTIME_MODE=docker
```

The Docker socket is mounted in `docker-compose.yml` for local Docker runtime experiments.

## Development

Run the full test suite:

```bash
mvn test
```

Run the API from source:

```bash
mvn -pl api -am spring-boot:run
```

Create a token:

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/token \
  -H 'Content-Type: application/json' \
  -d '{"subject":"admin","role":"ADMIN"}'
```

Register a node:

```bash
curl -X POST http://localhost:8080/api/v1/nodes \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"worker-1","host":"unix:///var/run/docker.sock","cpuCapacity":"4000m","memoryCapacity":"8Gi","diskCapacity":"50Gi","labels":{"zone":"local"}}'
```

Create a deployment:

```bash
curl -X POST http://localhost:8080/api/v1/deployments \
  -H "Authorization: Bearer $TOKEN" \
  -H 'Content-Type: application/json' \
  -d '{"name":"payment-service","image":"nginx:1.27","replicas":3,"cpuRequest":"250m","memoryRequest":"128Mi","storageRequest":"0","scheduler":"RESOURCE_AWARE"}'
```

## API Groups

- Cluster: `GET /api/v1/cluster`, `GET /api/v1/cluster/status`
- Nodes: `POST/GET /api/v1/nodes`, `GET/DELETE /api/v1/nodes/{id}`, `POST /api/v1/nodes/{id}/drain`, `POST /api/v1/nodes/{id}/cordon`
- Deployments: `POST/GET /api/v1/deployments`, `GET/PUT/DELETE /api/v1/deployments/{id}`, scale/pause/resume/rollback actions
- Pods: `GET /api/v1/pods`, `GET/DELETE /api/v1/pods/{id}`, `POST /api/v1/pods/{id}/restart`
- Services: `POST/GET /api/v1/services`
- Operations: `POST /api/v1/autoscaling`, `GET /api/v1/metrics`, `GET /api/v1/logs/{podId}`, `GET /api/v1/events`, `GET /api/v1/health`

## Notes

KubeMini does not use Kubernetes, `kubectl`, Minikube, or the Kubernetes Java client. Docker is the only container runtime integration, behind an application-layer port so other runtimes can be added later.
