package models

case class NotifierRequest(
    title: String,
//    message: String,
    priority: String = "High",
    tags: Seq[String] = Seq()
)
