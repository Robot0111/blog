package com.leolxthy.blog.Controller;

import com.leolxthy.blog.Entity.TableColumn;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    // 分类管理入口
    @GetMapping("/categories")
    public String manageCategories(Model model) {
//        setupModel(model, "分类", "categories", true);
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("ID", "80px"));
        columns.add(new TableColumn("名称", "auto"));
        columns.add(new TableColumn("描述", "150px"));
        columns.add(new TableColumn("创建时间", "200px"));

        model.addAttribute("pageTitle", "分类");
        model.addAttribute("columns", columns);
        model.addAttribute("apiKey", "categories");
        model.addAttribute("display", true);
        // 关键配置：指定新增按钮的跳转链接，而不是弹出模态框
//        model.addAttribute("addUrl", "/admin/articles/write");

        return "generic";
    }

    // 标签管理入口
    @GetMapping("/tags")
    public String manageTags(Model model) {
        // 标签通常没有“描述”字段，所以传 false
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("ID", "80px"));
        columns.add(new TableColumn("名称", "auto"));

        model.addAttribute("pageTitle", "标签");
        model.addAttribute("columns", columns);
        model.addAttribute("apiKey", "tags");
        model.addAttribute("display", false);

        return "generic";
    }
    @GetMapping("/articles")
    public String manageArticles(Model model) {
        List<TableColumn> columns = new ArrayList<>();
        columns.add(new TableColumn("ID", "60px"));
        columns.add(new TableColumn("标题", "auto"));
        columns.add(new TableColumn("分类", "120px"));
        columns.add(new TableColumn("状态", "100px"));
        columns.add(new TableColumn("发布时间", "180px"));

        model.addAttribute("pageTitle", "文章管理");
        model.addAttribute("columns", columns);
        model.addAttribute("apiKey", "articles");
        model.addAttribute("addUrl", "/admin"); // 点击“新增”跳转到独立写博客页面
        return "generic";
    }

}
