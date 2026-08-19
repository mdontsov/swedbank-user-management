plugins {
    base
}

tasks.register<Exec>("frontendInstall") {
    workingDir("frontend")
    commandLine("npm", "ci")
}

tasks.register<Exec>("frontendTest") {
    dependsOn("frontendInstall")
    workingDir("frontend")
    commandLine("npm", "test", "--", "--watch=false")
}

tasks.register<Exec>("frontendBuild") {
    dependsOn("frontendInstall")
    workingDir("frontend")
    commandLine("npm", "run", "build")
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

