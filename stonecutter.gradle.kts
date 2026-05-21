plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.20.1"
stonecutter.tasks {
    order("modrinth")
}