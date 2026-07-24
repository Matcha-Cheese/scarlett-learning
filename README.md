# 模块 09 实验 — 使用 GitHub Actions 持续集成
# Module 09 Lab — Continuous Integration with GitHub Actions

## 目标 | Objective
为 Stock Tracker Spring Boot 应用添加一个 GitHub Actions CI 工作流，使每次
`push` 和 `pull request` 都能自动构建项目并运行测试。

Add a GitHub Actions CI workflow to the Stock Tracker Spring Boot application so that every
push and pull request automatically builds the project and runs the tests.

## 前置条件 | Prerequisites
- 一个 GitHub 账号  
  A GitHub account
- 本地已安装 Git  
  Git installed locally
- 本地已有模块 06 的解答（或你自己完成的 stocks 应用）  
  The Module 06 solution (or your own completed stocks application) available locally
- 本实验不需要 Docker —— 测试使用 Mockito，不需要数据库  
  Docker not required for this lab — the tests use Mockito and do not need a database

## 概览 | Overview
你将会：
1. 将 stocks 应用推送到你自己的 GitHub 仓库
2. 创建 `.github/workflows/ci.yml` 工作流文件
3. 推送工作流并观察 GitHub Actions 执行
4. 人为引入一个测试失败并观察 CI 失败
5. 修复失败并观察 CI 再次变绿

You will:
1. Push the stocks application to your own GitHub repository
2. Create a `.github/workflows/ci.yml` workflow file
3. Push the workflow and watch GitHub Actions run it
4. Introduce a deliberate test failure and observe the CI failure
5. Fix the failure and watch CI go green again

---

## 步骤 | Steps

### 第 1 步 — 创建 GitHub 仓库 | Step 1 — Create a GitHub repository
1. 登录 GitHub，创建一个名为 `stock-tracker` 的新仓库  
   Log in to GitHub and create a new repository called `stock-tracker`
2. 保持仓库为空（不要在网页 UI 中添加 README 或 .gitignore）  
   Leave it empty (do not add README or .gitignore via the UI)

### 第 2 步 — 将应用推送到 GitHub | Step 2 — Push the application to GitHub
在模块 06 的解答目录（或你自己完成的 stocks 应用目录）执行：  
From the Module 06 solution directory (or your own completed stocks app):

```bash
git init
git add .
git commit -m "Initial commit - stocks REST API"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/stock-tracker.git
git push -u origin main
```

在 GitHub 上打开仓库，确认源码已成功上传。  
Open the repository on GitHub and confirm the source code is there.

---

### 第 3 步 — 创建工作流文件 | Step 3 — Create the workflow file
在你的项目中创建文件 `.github/workflows/ci.yml`。  
In your project, create the file `.github/workflows/ci.yml`.

完成下面每个 TODO：  
Complete each TODO:

```yaml
# TODO 1: 给工作流命名，例如 "CI" / Give the workflow a name, e.g. "CI"
name: ???

# TODO 2: 设置触发器 - 对 main 分支的 push 和 pull_request 都要触发
# TODO 2: Set the trigger - run on push AND pull_request to the main branch
on:
  push:
    branches: [ ??? ]
  pull_request:
    branches: [ ??? ]

jobs:
  build:
    # TODO 3: 选择运行器 - 使用最新的 Ubuntu 托管运行器
    # TODO 3: Choose a runner - use the latest Ubuntu hosted runner
    runs-on: ???

    steps:
      # TODO 4: 使用官方 action 检出仓库代码
      # TODO 4: Check out the repository code using the official action
      - name: Checkout source
        uses: actions/???@v4

      # TODO 5: 设置 Java 17（Temurin 发行版）并启用 Maven 缓存
      # TODO 5: Set up Java 17 (Temurin distribution) with Maven cache
      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '???'
          distribution: '???'
          cache: ???

      # TODO 6: 使用 Maven 构建项目，跳过测试
      # TODO 6: Build the project with Maven, skipping tests
      - name: Build with Maven
        run: mvn -B package -DskipTests

      # TODO 7: 运行测试
      # TODO 7: Run the tests
      - name: Run tests
        run: ???
```

---

### 第 4 步 — 推送工作流并观察运行 | Step 4 — Push the workflow and watch it run
```bash
git add .github/workflows/ci.yml
git commit -m "Add CI workflow"
git push
```

进入你在 GitHub 上的仓库，点击 **Actions** 标签页。  
Go to your repository on GitHub and click the **Actions** tab.

几秒内你应该能看到一次工作流运行。  
You should see the workflow run appear within a few seconds.

点开这次运行，再进入 `build` 作业，展开每个步骤查看日志输出。  
Click into the run, then into the `build` job, and expand each step to see the log output.

运行应以绿色对勾完成。  
The run should complete with a green tick.

---

### 第 5 步 — 人为引入一个失败 | Step 5 — Introduce a deliberate failure
打开 `src/main/java/com/stocks/service/StockServiceImpl.java`。  
Open `src/main/java/com/stocks/service/StockServiceImpl.java`.

找到 `addStock` 方法，修改重复检查条件，让它总是抛异常：  
Find the `addStock` method and change the duplicate-check condition so it always throws:

```java
// Temporarily break the duplicate check
throw new IllegalArgumentException("Always broken: " + stock.symbol());
```

提交并推送：  
Commit and push:

```bash
git add src/main/java/com/stocks/service/StockServiceImpl.java
git commit -m "Break addStock for CI demo"
git push
```

观察 **Actions** 标签页 —— 这次运行应变为红色。点进失败记录，查看是哪个测试捕获了问题以及原因。  
Watch the **Actions** tab — the run should turn red. Click into the failure and find which test caught it and why.

---

### 第 6 步 — 修复失败并恢复绿色 | Step 6 — Fix the failure and go green
撤销你对 `StockServiceImpl` 的改动：  
Revert your change to `StockServiceImpl`:

```bash
git revert HEAD
git push
```

再次观察 Actions 标签页 —— CI 应恢复绿色。  
Watch the Actions tab — CI should go green again.

---

### 第 7 步 — 添加构建状态徽章（可选进阶） | Step 7 — Add a build status badge (stretch)
GitHub 会为你的工作流生成徽章 URL。可在以下位置找到：  
GitHub generates a badge URL for your workflow. Find it at:

**Actions 标签页 > 你的工作流 > 右上角 "..." 菜单 > Create status badge**  
**Actions tab > your workflow > top-right "..." menu > Create status badge**

复制 markdown 并粘贴到你的 `README.md`：  
Copy the markdown and paste it into your `README.md`:

```markdown
![CI](https://github.com/YOUR-USERNAME/stock-tracker/actions/workflows/ci.yml/badge.svg)
```

提交并推送后，徽章会显示在仓库首页。  
Commit and push — the badge appears on your repo's home page.

---

## 验收标准 | Acceptance Criteria
- 工作流文件位于 `.github/workflows/ci.yml`  
  The workflow file is at `.github/workflows/ci.yml`
- 向 `main` 分支推送时会自动触发工作流  
  A push to `main` triggers the workflow automatically
- Actions UI 中构建步骤和测试步骤分别独立显示  
  Both the build step and the test step appear as separate steps in the Actions UI
- 人为引入测试失败后，工作流运行显示红色  
  A deliberate test failure causes the workflow run to show red
- 修复失败并再次推送后，CI 恢复绿色  
  Fixing the failure and pushing again returns CI to green

## 关键问题 | Key Questions
1. 作为触发器，`push` 与 `pull_request` 有什么区别？  
   What is the difference between `push` and `pull_request` as triggers?
2. 为什么要把 `mvn package -DskipTests` 和 `mvn test` 分成两个步骤？  
   Why do we separate `mvn package -DskipTests` and `mvn test` into two steps?
3. `cache: maven` 的作用是什么？为什么它会影响构建速度？  
   What does `cache: maven` do, and why does it matter for build speed?
4. 什么是 GitHub 托管运行器？默认预装了哪些内容？  
   What is a GitHub-hosted runner, and what is installed on it by default?
5. 如果你的测试需要真实的 MySQL 数据库，应该如何在工作流中提供它？  
   If your tests needed a real MySQL database, how would you provide one in the workflow?
