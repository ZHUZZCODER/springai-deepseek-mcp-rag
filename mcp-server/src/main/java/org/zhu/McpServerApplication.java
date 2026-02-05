package org.zhu;

import io.github.cdimascio.dotenv.Dotenv;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.zhu.mcp.tool.DateTool;
import org.zhu.mcp.tool.EmailTool;
import org.zhu.mcp.tool.ProductTool;

@MapperScan("org.zhu.mapper")
@SpringBootApplication
public class McpServerApplication {
    public static void main(String[] args) {
        // 加载.env文件
        Dotenv dotenv =  Dotenv.configure().ignoreIfMissing().load();
        // 把.env文件中的变量设置到环境变量中
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
        SpringApplication.run(McpServerApplication.class, args);
    }

    /**
     * 开发第一个mcp工具，DateTool
     * 注册mcp工具
     * http://localhost:9060/sse 检查配置是否存在问题
     * @param dataTool
     * @return
     */
    @Bean
    public ToolCallbackProvider reqistMcpTools(DateTool dataTool, EmailTool emailTool, ProductTool productTool){
        return MethodToolCallbackProvider.builder()
                .toolObjects(dataTool,emailTool,productTool)
                .build();
    }
}
