package model;

import java.io.Serializable;

public class TaiKhoan implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final String tenDangNhap;
    private final String vaiTro;

    public TaiKhoan(int id, String tenDangNhap, String vaiTro) {
        this.id = id;
        this.tenDangNhap = tenDangNhap;
        this.vaiTro = vaiTro;
    }

    public int getId() {
        return id;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public String getVaiTro() {
        return vaiTro;
    }

}