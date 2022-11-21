package models

case class NotifierResponse(
    id: String,
    time: Long,
    event: String,
    topic: String,
    message: String,
    title: Option[String],
    priority: Option[Int],
    tags: Option[Seq[String]]
)
