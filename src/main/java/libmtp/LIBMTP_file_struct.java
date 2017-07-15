package libmtp;
import libmtp.LibmtpLibrary.LIBMTP_filetype_t;
import org.bridj.BridJ;
import org.bridj.IntValuedEnum;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.CLong;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * MTP file struct<br>
 * <i>native declaration : /usr/local/Cellar/libmtp/1.1.13/include/libmtp.h:623</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("mtp")
public class LIBMTP_file_struct extends StructObject {
	static {
		BridJ.register();
	}
	/** < Unique item ID */
	@Field(0) 
	public int item_id() {
		return this.io.getIntField(this, 0);
	}
	/** < Unique item ID */
	@Field(0) 
	public LIBMTP_file_struct item_id(int item_id) {
		this.io.setIntField(this, 0, item_id);
		return this;
	}
	/** < ID of parent folder */
	@Field(1) 
	public int parent_id() {
		return this.io.getIntField(this, 1);
	}
	/** < ID of parent folder */
	@Field(1) 
	public LIBMTP_file_struct parent_id(int parent_id) {
		this.io.setIntField(this, 1, parent_id);
		return this;
	}
	/** < ID of storage holding this file */
	@Field(2) 
	public int storage_id() {
		return this.io.getIntField(this, 2);
	}
	/** < ID of storage holding this file */
	@Field(2) 
	public LIBMTP_file_struct storage_id(int storage_id) {
		this.io.setIntField(this, 2, storage_id);
		return this;
	}
	/**
	 * < Filename of this file<br>
	 * C type : char*
	 */
	@Field(3) 
	public Pointer<Byte > filename() {
		return this.io.getPointerField(this, 3);
	}
	/**
	 * < Filename of this file<br>
	 * C type : char*
	 */
	@Field(3) 
	public LIBMTP_file_struct filename(Pointer<Byte > filename) {
		this.io.setPointerField(this, 3, filename);
		return this;
	}
	/** < Size of file in bytes */
	@Field(4) 
	public long filesize() {
		return this.io.getLongField(this, 4);
	}
	/** < Size of file in bytes */
	@Field(4) 
	public LIBMTP_file_struct filesize(long filesize) {
		this.io.setLongField(this, 4, filesize);
		return this;
	}
	/** < Date of last alteration of the file */
	@CLong 
	@Field(5) 
	public long modificationdate() {
		return this.io.getCLongField(this, 5);
	}
	/** < Date of last alteration of the file */
	@CLong 
	@Field(5) 
	public LIBMTP_file_struct modificationdate(long modificationdate) {
		this.io.setCLongField(this, 5, modificationdate);
		return this;
	}
	/**
	 * < Filetype used for the current file<br>
	 * C type : LIBMTP_filetype_t
	 */
	@Field(6) 
	public IntValuedEnum<LIBMTP_filetype_t > filetype() {
		return this.io.getEnumField(this, 6);
	}
	/**
	 * < Filetype used for the current file<br>
	 * C type : LIBMTP_filetype_t
	 */
	@Field(6) 
	public LIBMTP_file_struct filetype(IntValuedEnum<LIBMTP_filetype_t > filetype) {
		this.io.setEnumField(this, 6, filetype);
		return this;
	}
	/**
	 * < Next file in list or NULL if last file<br>
	 * C type : LIBMTP_file_t*
	 */
	@Field(7) 
	public Pointer<LIBMTP_file_struct > next() {
		return this.io.getPointerField(this, 7);
	}
	/**
	 * < Next file in list or NULL if last file<br>
	 * C type : LIBMTP_file_t*
	 */
	@Field(7) 
	public LIBMTP_file_struct next(Pointer<LIBMTP_file_struct > next) {
		this.io.setPointerField(this, 7, next);
		return this;
	}
	public LIBMTP_file_struct() {
		super();
	}
	public LIBMTP_file_struct(Pointer pointer) {
		super(pointer);
	}
}
