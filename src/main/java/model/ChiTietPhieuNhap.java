package model;

import java.math.BigDecimal;

public class ChiTietPhieuNhap {

    private final int taiLieuId;
    private final String maTaiLieu;
    private final String tenTaiLieu;
    private final int soLuong;
    private final BigDecimal donGia;

    public ChiTietPhieuNhap(int taiLieuId, String maTaiLieu,
                            String tenTaiLieu, int soLuong,
                            BigDecimal donGia) {
        this.taiLieuId = taiLieuId;
        this.maTaiLieu = maTaiLieu;
        this.tenTaiLieu = tenTaiLieu;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public int getTaiLieuId() {
        return taiLieuId;
    }

    public String getMaTaiLieu() {
        return maTaiLieu;
    }

    public String getTenTaiLieu() {
        return tenTaiLieu;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public BigDecimal getThanhTien() {
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }
}