FROM ubuntu:latest
LABEL authors="janine"

# Install cURL
RUN apt-get update && apt-get install -y curl && apt-get install -y jq

# Set the working directory
WORKDIR /app
ADD config.txt .
ADD next.txt .
ADD auth.sh .
RUN chmod +x auth.sh

# Your additional configuration
CMD ["bash"]