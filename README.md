# springai-deepseek-mcp-rag
## 1.本地运行此项目

1. 需要在虚拟机或服务器安装下面这些docker服务
   ![image-20260205153049253](C:/Users/23376/AppData/Roaming/Typora/typora-user-images/image-20260205153049253.png)
2. idea打开此项目，在mcp-client和mcp-server下，新建.env配置对应的环境变量
3. 然后就能开始运行项目了

## 2.服务器部署此项目

1. 部署文件在"docker部署"文件夹中

2. 在本地将mcp-client和mcp-server的jar包打好之后，放到docker部署\deepseek-mcp-rag文件夹下

3. 上传deepseek-mcp-rag文件夹到服务器对应位置

4. 在服务器deepseek-mcp-rag目录执行下面命令，打包成docker镜像

   ```
   docker build -t mcp-server-image -f Dockerfile.server .
   docker build -t mcp-client-image -f Dockerfile.client .
   ```

   5.运行docker镜像，在deepseek-mcp-rag下，执行下面命令

```
docker compose up -d
```

