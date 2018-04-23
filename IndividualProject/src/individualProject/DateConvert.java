package individualProject;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class DateConvert {
	public static String dateFormat(Timestamp date) {
		SimpleDateFormat myFormat = new SimpleDateFormat ("dd/MM/yyyy HH:mm");
		return myFormat.format(date);
	}
}
