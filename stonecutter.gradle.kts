plugins {
    id("dev.kikugie.stonecutter")
}
stonecutter active "1.21"
stonecutter.tasks {
    order("modrinth")
}