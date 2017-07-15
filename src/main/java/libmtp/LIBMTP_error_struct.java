package libmtp;
import libmtp.LibmtpLibrary.LIBMTP_error_number_t;
import org.bridj.BridJ;
import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * A data structure to hold errors from the library.<br>
 * <i>native declaration : /usr/local/Cellar/libmtp/1.1.13/include/libmtp.h:482</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("mtp")
public class LIBMTP_error_struct extends StructObject {
	static {
		BridJ.register();
	}
	/** C type : LIBMTP_error_number_t */
	@Field(0) 
	public IntValuedEnum<LIBMTP_error_number_t > errornumber() {
		return this.io.getEnumField(this, 0);
	}
	/** C type : LIBMTP_error_number_t */
	@Field(0) 
	public LIBMTP_error_struct errornumber(IntValuedEnum<LIBMTP_error_number_t > errornumber) {
		this.io.setEnumField(this, 0, errornumber);
		return this;
	}
	/** C type : char* */
	@Field(1) 
	public Pointer<Byte > error_text() {
		return this.io.getPointerField(this, 1);
	}
	/** C type : char* */
	@Field(1) 
	public LIBMTP_error_struct error_text(Pointer<Byte > error_text) {
		this.io.setPointerField(this, 1, error_text);
		return this;
	}
	/** C type : LIBMTP_error_t* */
	@Field(2) 
	public Pointer<LIBMTP_error_struct > next() {
		return this.io.getPointerField(this, 2);
	}
	/** C type : LIBMTP_error_t* */
	@Field(2) 
	public LIBMTP_error_struct next(Pointer<LIBMTP_error_struct > next) {
		this.io.setPointerField(this, 2, next);
		return this;
	}
	public LIBMTP_error_struct() {
		super();
	}
	public LIBMTP_error_struct(Pointer pointer) {
		super(pointer);
	}
}
