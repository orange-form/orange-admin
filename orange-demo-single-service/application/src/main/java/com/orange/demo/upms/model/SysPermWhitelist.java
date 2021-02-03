package com.orange.demo.upms.model;

import lombok.Data;

import javax.persistence.*;

/**
 * 白名单实体对象。
 *
 * @author Jerry
 * @date 2020-09-24
 */
@Data
@Table(name = "zz_sys_perm_whitelist")
public class SysPermWhitelist {

    /**
     * 权限资源的URL。
     */
    @Id
    @Column(name = "perm_url")
    private String permUrl;

    /**
     * 权限资源所属模块名字(通常是Controller的名字)。
     */
    @Column(name = "module_name")
    private String moduleName;

    /**
     * 权限的名称。
     */
    @Column(name = "perm_name")
    private String permName;
}
