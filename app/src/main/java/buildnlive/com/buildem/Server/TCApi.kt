package buildnlive.com.buildem.Server


import buildnlive.com.buildem.Server.Request.AttendanceRequest
import buildnlive.com.buildem.Server.Response.AttendanceResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


interface TCApi {
//  <--------------------------------------------RXJAVA------------------------------------------------>

@POST("index.php?r=Attendance/SendAttendanceUser")
    fun callSendAttendance(@Body attendanceRequest: AttendanceRequest): Single<Response<AttendanceResponse>>

}