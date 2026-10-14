# Setup on a New Laptop – Pull and Run the Monitoring Project

Git repository: `https://github.com/msaravananguru/money.monitoring.git` (branch `main`)

The code comes from GitHub. **The database data does not come with it** – the new laptop gets an empty database, created from `database_scripts.sql`.

---

## Step 0 – On the OLD laptop (do this first)

The new laptop can only pull what has been pushed. Open Command Prompt in the `money.monitoring` project folder:

```bash
git status
git add .
git commit -m "Add Category Manager API and Information docs"
git push origin main
```

Check on GitHub in your browser that the latest files are there (including the `Information` folder).

---

## Step 1 – Install the software on the NEW laptop

| Software | Version | Check after install |
|---|---|---|
| Git | Latest | `git --version` |
| JDK | **21** (required by `pom.xml`) | `java -version` shows 21 |
| MySQL Server + Workbench | 8.x | Workbench opens and connects |
| Eclipse IDE for Enterprise Java (or STS) | Latest | Opens |
| Postman | Latest | Opens |
| Node.js | 20.19 or newer (or 22+), needed for the UI | `node -v` |

Notes:
- Remember the MySQL `root` password you set during installation.
- Maven does not need a separate install. The project has its own wrapper (`mvnw.cmd`).

---

## Step 2 – Pull (clone) the project

Open Command Prompt:

```bash
cd E:\
git clone https://github.com/msaravananguru/money.monitoring.git
cd money.monitoring
```

If GitHub asks you to sign in, use your GitHub username and a Personal Access Token as the password (GitHub → Settings → Developer settings → Personal access tokens). Normal account passwords no longer work for git.

Later, to get new changes made on the other laptop:

```bash
git pull origin main
```

---

## Step 3 – Create the database

1. Open **MySQL Workbench** and connect to the local instance (as `root`).
2. Open the file `Information\database_scripts.sql` (File → Open SQL Script).
3. Run the **Initial setup** part only:

```sql
CREATE DATABASE monitoring_db;
CREATE USER 'monitor_user'@'localhost' IDENTIFIED BY 'monitoring_db';
GRANT ALL PRIVILEGES ON monitoring_db.* TO 'monitor_user'@'localhost';
FLUSH PRIVILEGES;
```

4. Do **not** create the `category_manager` table by hand. The app creates it on first start.

If you ever see `database exists` or `user already exists`, that part was already done and you can skip it.

---

## Step 4 – Check the connection settings

Open `src\main\resources\application.properties`. It should contain:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/monitoring_db
spring.datasource.username=monitor_user
spring.datasource.password=monitoring_db
spring.jpa.hibernate.ddl-auto=update
```

These must match what you created in Step 3. If your MySQL uses a port other than 3306, change it in the URL.

---

## Step 5 – Open the project in Eclipse

1. **File → Import → Maven → Existing Maven Projects → Next**.
2. Browse to the cloned `money.monitoring` folder and click **Finish**.
3. Wait for the bottom-right progress bar (downloading dependencies) to finish. The first time can take a few minutes and needs internet.
4. Set Java 21: right-click project → **Properties → Java Compiler** → compiler compliance **21**. Also check **Window → Preferences → Java → Installed JREs** has JDK 21 ticked.
5. Right-click project → **Maven → Update Project** → tick **Force Update** → OK.
6. Open the **Problems** tab. It should show no errors.

---

## Step 6 – Run the application

**Option A – in Eclipse**
Right-click `MonitoringApplication.java` → **Run As → Java Application**.

**Option B – from Command Prompt**
```bash
cd E:\money.monitoring
mvnw.cmd spring-boot:run
```

The app has started when the Console shows `Started MonitoringApplication` and `Tomcat started on port 8080`.

---

## Step 7 – Verify the database

In MySQL Workbench run:

```sql
USE monitoring_db;
SHOW TABLES;
SELECT * FROM category_manager;
```

You should see the `category_manager` table (empty at first).

---

## Step 8 – Test in Postman

- **Method:** `POST`
- **URL:** `http://localhost:8080/api/category-manager/add`
- **Body:** raw → JSON

```json
{
  "userId": 101,
  "category": "Food",
  "reason": "Monthly groceries"
}
```

Expected: `201 Created`. Sending the same `userId` and `reason` again returns `409 Conflict`. Then run the `SELECT` again in Workbench to see the row.

---

## Step 9 – Run the UI (React)

The UI is in the `frontend` folder. Keep the backend from Step 6 running, then open a **second** Command Prompt:

```bash
cd money.monitoring\frontend
npm install
npm run dev
```

- `npm install` is needed only the first time (it needs internet).
- Open **http://localhost:5173** in the browser.
- Enter User ID, Category and Reason, then click **Add category**.
- The UI sends requests to the backend on port 8080 automatically, so no extra setting is needed.

---

## Troubleshooting

| Problem | Cause | Fix |
|---|---|---|
| `Access denied for user 'monitor_user'` | User or password not created | Repeat Step 3 and check Step 4 |
| `Unknown database 'monitoring_db'` | Database not created | Run `CREATE DATABASE monitoring_db;` |
| `Communications link failure` / `Connection refused` | MySQL service not running | Press Win+R → `services.msc` → start **MySQL80** |
| `Port 8080 was already in use` | Another app uses 8080 | Add `server.port=8081` to `application.properties` and use 8081 in Postman |
| `release version 21 not supported` / wrong Java | Old JDK used | Install JDK 21 and select it in Eclipse (Step 5, point 4) |
| Red errors after import | Dependencies not downloaded | Check internet, then Maven → Update Project (force update) |
| `npm` is not recognized | Node.js not installed | Install Node.js (Step 1), then open a new Command Prompt |
| UI shows `Cannot reach the server` | Backend is not running | Start the backend (Step 6), then try again |
| `npm install` fails or shows engine warnings | Node.js too old | Install Node.js 20.19 or newer |
| `git push/pull` asks for a password | GitHub needs a token | Use a Personal Access Token (Step 2) |
| `category_manager` table missing | App did not start | Read the red error in the Eclipse Console |
| Tables are empty on the new laptop | Data is not part of git | Expected. Re-add data through the API, or export and import it from the old MySQL with Workbench (Server → Data Export / Data Import) |

---

## Quick checklist

- [ ] Old laptop: `git push` done
- [ ] Git, JDK 21, MySQL, Eclipse, Postman installed
- [ ] `git clone` done
- [ ] `monitoring_db` and `monitor_user` created
- [ ] Project imported in Eclipse, no errors in Problems tab
- [ ] App started on port 8080
- [ ] Postman returns `201 Created`
- [ ] `npm install` and `npm run dev` done in `frontend`, UI opens at http://localhost:5173

*The database password is stored in `application.properties`, which is in the Git repository. Use a different password if the repository is public or shared.*
