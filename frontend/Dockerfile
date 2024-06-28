FROM node:16.17.0 as builder

# 작업 폴더를 만들고 npm 설치
RUN mkdir /app
WORKDIR /app
ENV PATH /app/node_modules/.bin:$PATH
COPY package.json /app/package.json
RUN npm install

# 소스를 작업폴더로 복사하고 빌드
COPY public /app/public
COPY src /app/src
RUN npm run build

#################################################################

FROM nginx:1.21.6

# work dir 고정
WORKDIR /home

# 위에서 생성한 앱의 빌드산출물을 nginx의 샘플 앱이 사용하던 폴더로 이동
COPY --from=builder /app/build /home/build

# 80 포트 오픈
EXPOSE 80

# container 실행 시 자동으로 실행할 command. nginx 시작함
CMD ["nginx", "-g", "daemon off;"]
