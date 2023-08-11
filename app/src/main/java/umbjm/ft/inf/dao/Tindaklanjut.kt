package umbjm.ft.inf.dao

import java.sql.Timestamp

class Tindaklanjut (
    var id:Int? = 0,
    var pengaduan_id:Int? = 0,
    var Status: String? = null,
    var created_at: Timestamp? = null,
    var updated_at: Timestamp? = null,
)