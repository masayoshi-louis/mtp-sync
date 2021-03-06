package libmtp;
import org.bridj.BridJ;
import org.bridj.Pointer;
import org.bridj.StructObject;
import org.bridj.ann.Field;
import org.bridj.ann.Library;
/**
 * Main MTP device object struct<br>
 * <i>native declaration : /usr/local/Cellar/libmtp/1.1.13/include/libmtp.h:564</i><br>
 * This file was autogenerated by <a href="http://jnaerator.googlecode.com/">JNAerator</a>,<br>
 * a tool written by <a href="http://ochafik.com/">Olivier Chafik</a> that <a href="http://code.google.com/p/jnaerator/wiki/CreditsAndLicense">uses a few opensource projects.</a>.<br>
 * For help, please visit <a href="http://nativelibs4java.googlecode.com/">NativeLibs4Java</a> or <a href="http://bridj.googlecode.com/">BridJ</a> .
 */
@Library("mtp")
public class LIBMTP_mtpdevice_struct extends StructObject {
	static {
		BridJ.register();
	}
	@Field(0) 
	public byte object_bitsize() {
		return this.io.getByteField(this, 0);
	}
	@Field(0) 
	public LIBMTP_mtpdevice_struct object_bitsize(byte object_bitsize) {
		this.io.setByteField(this, 0, object_bitsize);
		return this;
	}
	/** C type : void* */
	@Field(1) 
	public Pointer<? > params() {
		return this.io.getPointerField(this, 1);
	}
	/** C type : void* */
	@Field(1) 
	public LIBMTP_mtpdevice_struct params(Pointer<? > params) {
		this.io.setPointerField(this, 1, params);
		return this;
	}
	/** C type : void* */
	@Field(2) 
	public Pointer<? > usbinfo() {
		return this.io.getPointerField(this, 2);
	}
	/** C type : void* */
	@Field(2) 
	public LIBMTP_mtpdevice_struct usbinfo(Pointer<? > usbinfo) {
		this.io.setPointerField(this, 2, usbinfo);
		return this;
	}
	/** C type : LIBMTP_devicestorage_t* */
	@Field(3) 
	public Pointer<LIBMTP_devicestorage_struct > storage() {
		return this.io.getPointerField(this, 3);
	}
	/** C type : LIBMTP_devicestorage_t* */
	@Field(3) 
	public LIBMTP_mtpdevice_struct storage(Pointer<LIBMTP_devicestorage_struct > storage) {
		this.io.setPointerField(this, 3, storage);
		return this;
	}
	/** C type : LIBMTP_error_t* */
	@Field(4) 
	public Pointer<LIBMTP_error_struct > errorstack() {
		return this.io.getPointerField(this, 4);
	}
	/** C type : LIBMTP_error_t* */
	@Field(4) 
	public LIBMTP_mtpdevice_struct errorstack(Pointer<LIBMTP_error_struct > errorstack) {
		this.io.setPointerField(this, 4, errorstack);
		return this;
	}
	@Field(5) 
	public byte maximum_battery_level() {
		return this.io.getByteField(this, 5);
	}
	@Field(5) 
	public LIBMTP_mtpdevice_struct maximum_battery_level(byte maximum_battery_level) {
		this.io.setByteField(this, 5, maximum_battery_level);
		return this;
	}
	@Field(6) 
	public int default_music_folder() {
		return this.io.getIntField(this, 6);
	}
	@Field(6) 
	public LIBMTP_mtpdevice_struct default_music_folder(int default_music_folder) {
		this.io.setIntField(this, 6, default_music_folder);
		return this;
	}
	@Field(7) 
	public int default_playlist_folder() {
		return this.io.getIntField(this, 7);
	}
	@Field(7) 
	public LIBMTP_mtpdevice_struct default_playlist_folder(int default_playlist_folder) {
		this.io.setIntField(this, 7, default_playlist_folder);
		return this;
	}
	@Field(8) 
	public int default_picture_folder() {
		return this.io.getIntField(this, 8);
	}
	@Field(8) 
	public LIBMTP_mtpdevice_struct default_picture_folder(int default_picture_folder) {
		this.io.setIntField(this, 8, default_picture_folder);
		return this;
	}
	@Field(9) 
	public int default_video_folder() {
		return this.io.getIntField(this, 9);
	}
	@Field(9) 
	public LIBMTP_mtpdevice_struct default_video_folder(int default_video_folder) {
		this.io.setIntField(this, 9, default_video_folder);
		return this;
	}
	@Field(10) 
	public int default_organizer_folder() {
		return this.io.getIntField(this, 10);
	}
	@Field(10) 
	public LIBMTP_mtpdevice_struct default_organizer_folder(int default_organizer_folder) {
		this.io.setIntField(this, 10, default_organizer_folder);
		return this;
	}
	@Field(11) 
	public int default_zencast_folder() {
		return this.io.getIntField(this, 11);
	}
	@Field(11) 
	public LIBMTP_mtpdevice_struct default_zencast_folder(int default_zencast_folder) {
		this.io.setIntField(this, 11, default_zencast_folder);
		return this;
	}
	@Field(12) 
	public int default_album_folder() {
		return this.io.getIntField(this, 12);
	}
	@Field(12) 
	public LIBMTP_mtpdevice_struct default_album_folder(int default_album_folder) {
		this.io.setIntField(this, 12, default_album_folder);
		return this;
	}
	@Field(13) 
	public int default_text_folder() {
		return this.io.getIntField(this, 13);
	}
	@Field(13) 
	public LIBMTP_mtpdevice_struct default_text_folder(int default_text_folder) {
		this.io.setIntField(this, 13, default_text_folder);
		return this;
	}
	/** C type : void* */
	@Field(14) 
	public Pointer<? > cd() {
		return this.io.getPointerField(this, 14);
	}
	/** C type : void* */
	@Field(14) 
	public LIBMTP_mtpdevice_struct cd(Pointer<? > cd) {
		this.io.setPointerField(this, 14, cd);
		return this;
	}
	/** C type : LIBMTP_device_extension_t* */
	@Field(15) 
	public Pointer<LIBMTP_device_extension_struct > extensions() {
		return this.io.getPointerField(this, 15);
	}
	/** C type : LIBMTP_device_extension_t* */
	@Field(15) 
	public LIBMTP_mtpdevice_struct extensions(Pointer<LIBMTP_device_extension_struct > extensions) {
		this.io.setPointerField(this, 15, extensions);
		return this;
	}
	@Field(16) 
	public int cached() {
		return this.io.getIntField(this, 16);
	}
	@Field(16) 
	public LIBMTP_mtpdevice_struct cached(int cached) {
		this.io.setIntField(this, 16, cached);
		return this;
	}
	/** C type : LIBMTP_mtpdevice_t* */
	@Field(17) 
	public Pointer<LIBMTP_mtpdevice_struct > next() {
		return this.io.getPointerField(this, 17);
	}
	/** C type : LIBMTP_mtpdevice_t* */
	@Field(17) 
	public LIBMTP_mtpdevice_struct next(Pointer<LIBMTP_mtpdevice_struct > next) {
		this.io.setPointerField(this, 17, next);
		return this;
	}
	public LIBMTP_mtpdevice_struct() {
		super();
	}
	public LIBMTP_mtpdevice_struct(Pointer pointer) {
		super(pointer);
	}
}
