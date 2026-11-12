package model;

public class TaiLieu {

    private int id;
    private String maTaiLieu;
    private String tenTaiLieu;
    private String loaiTaiLieu;
    private String tacGia;
    private String nhaXuatBan;
    private Integer namXuatBan;
    private String moTa;
    private int soLuong;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaTaiLieu() {
        return maTaiLieu;
    }

    public void setMaTaiLieu(String maTaiLieu) {
        this.maTaiLieu = maTaiLieu;
    }

    public String getTenTaiLieu() {
        return tenTaiLieu;
    }

    public void setTenTaiLieu(String tenTaiLieu) {
        this.tenTaiLieu = tenTaiLieu;
    }

    public String getLoaiTaiLieu() {
        return loaiTaiLieu;
    }

    public void setLoaiTaiLieu(String loaiTaiLieu) {
        this.loaiTaiLieu = loaiTaiLieu;
    }

    public String getTacGia() {
        return tacGia;
    }

    public void setTacGia(String tacGia) {
        this.tacGia = tacGia;
    }

    public String getNhaXuatBan() {
        return nhaXuatBan;
    }

    public void setNhaXuatBan(String nhaXuatBan) {
        this.nhaXuatBan = nhaXuatBan;
    }

    public Integer getNamXuatBan() {
        return namXuatBan;
    }

    public void setNamXuatBan(Integer namXuatBan) {
        this.namXuatBan = namXuatBan;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getTenLoaiTaiLieu() {
        if (loaiTaiLieu == null) {
            return "";
        }

        return switch (loaiTaiLieu) {
            case "SACH" -> "Sách";
            case "GIAO_TRINH" -> "Giáo trình";
            case "TAP_CHI" -> "Tạp chí";
            default -> loaiTaiLieu;
        };
    }
}