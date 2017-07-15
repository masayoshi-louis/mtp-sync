package libmtp;
import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * MTP Folder structure<br>
 * <i>native declaration : /usr/local/Cellar/libmtp/1.1.13/include/libmtp.h:695</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("mtp")
public class LIBMTP_folder_struct extends StructObject {
	static {
		BridJ.register();
	}
	/** < Unique folder ID */
	@Field(0) 
	public int folder_id() {
		return this.io.getIntField(this, 0);
	}
	/** < Unique folder ID */
	@Field(0) 
	public LIBMTP_folder_struct folder_id(int folder_id) {
		this.io.setIntField(this, 0, folder_id);
		return this;
	}
	/** < ID of parent folder */
	@Field(1) 
	public int parent_id() {
		return this.io.getIntField(this, 1);
	}
	/** < ID of parent folder */
	@Field(1) 
	public LIBMTP_folder_struct parent_id(int parent_id) {
		this.io.setIntField(this, 1, parent_id);
		return this;
	}
	/** < ID of storage holding this folder */
	@Field(2) 
	public int storage_id() {
		return this.io.getIntField(this, 2);
	}
	/** < ID of storage holding this folder */
	@Field(2) 
	public LIBMTP_folder_struct storage_id(int storage_id) {
		this.io.setIntField(this, 2, storage_id);
		return this;
	}
	/**
	 * < Name of folder<br>
	 * C type : char*
	 */
	@Field(3) 
	public Pointer<Byte > name() {
		return this.io.getPointerField(this, 3);
	}
	/**
	 * < Name of folder<br>
	 * C type : char*
	 */
	@Field(3) 
	public LIBMTP_folder_struct name(Pointer<Byte > name) {
		this.io.setPointerField(this, 3, name);
		return this;
	}
	/**
	 * < Next folder at same level or NULL if no more<br>
	 * C type : LIBMTP_folder_t*
	 */
	@Field(4) 
	public Pointer<LIBMTP_folder_struct > sibling() {
		return this.io.getPointerField(this, 4);
	}
	/**
	 * < Next folder at same level or NULL if no more<br>
	 * C type : LIBMTP_folder_t*
	 */
	@Field(4) 
	public LIBMTP_folder_struct sibling(Pointer<LIBMTP_folder_struct > sibling) {
		this.io.setPointerField(this, 4, sibling);
		return this;
	}
	/**
	 * < Child folder or NULL if no children<br>
	 * C type : LIBMTP_folder_t*
	 */
	@Field(5) 
	public Pointer<LIBMTP_folder_struct > child() {
		return this.io.getPointerField(this, 5);
	}
	/**
	 * < Child folder or NULL if no children<br>
	 * C type : LIBMTP_folder_t*
	 */
	@Field(5) 
	public LIBMTP_folder_struct child(Pointer<LIBMTP_folder_struct > child) {
		this.io.setPointerField(this, 5, child);
		return this;
	}
	public LIBMTP_folder_struct() {
		super();
	}
	public LIBMTP_folder_struct(Pointer pointer) {
		super(pointer);
	}
}
