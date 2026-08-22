plugins {
    base
}

val npmExecutable = if (System.getProperty("os.name").startsWith("Windows", ignoreCase = true)) {
    "npm.cmd"
} else {
    "npm"
}

tasks.register<Exec>("frontendInstall") {
    workingDir("frontend")
    commandLine(npmExecutable, "ci")
}

tasks.register<Exec>("frontendTest") {
    dependsOn("frontendInstall")
    workingDir("frontend")
    commandLine(npmExecutable, "test", "--", "--watch=false")
}

tasks.register<Exec>("frontendBuild") {
    dependsOn("frontendInstall")
    workingDir("frontend")
    commandLine(npmExecutable, "run", "build")
}

tasks.named("check") {
    dependsOn(":backend:test", "frontendTest")
}

tasks.register<Exec>("composeUp") {
    commandLine("docker", "compose", "up", "--build")
}

tasks.register<Exec>("composeDown") {
    commandLine("docker", "compose", "down")
}
