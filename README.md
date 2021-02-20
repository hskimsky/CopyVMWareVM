# CopyVMWareVM

* 원본 VM 으로부터 복사 후 파일명들을 바꾸고 내용을 알맞게 바꿉니다.
* IP, HOSTNAME 을 자동으로 설정합니다.

# Build

```shell
git clone https://github.com/HaNeul-Kim/CopyVMWareVM  
cd CopyVMWareVM  
mvn clean install -DskipTests dependency:sources
java -jar target/CopyVMWare-0.5.jar --sourcePath /path/to/source/vm --sourceVMName centos_6.7_template --targetPath /path/to/target/vm --targetVMNames test1,test2 --encoding UTF-8
```

# Preparation

## Template VM

host 의 특정 directory 를 vm 에 공유 설정함

1. VM 우클릭 > Settings > Options 탭
1. Shared Folders 선택 > Always enabled 선택
1. Add...
1. 공유할 folder 선택
1. 이후 작업은 본인 환경에 맞도록

## VM Workstation

vm 시작시 shared folder disabled is default 라면서 shared folder 가 disable 되지 않기 위해

1. Edit > Preferences
1. Enable all shared folders by default 체크

## Template VM Power On

```shell
echo "~/set_auto.sh" >> /etc/rc.d/rc.local
chmod 755 /etc/rc.d/rc.local
```

```shell
vim ~/set_auto.sh
```

```shell
#! /bin/bash
AUTO_CONF_DIR=/mnt/hgfs/conf
if [ $# -gt 0 ] ; then
  AUTO_CONF_DIR=$1
fi
if [ ! -d ${AUTO_CONF_DIR} ] ; then
  echo "dost not exists ${AUTO_CONF_DIR}"
  exit 1
fi

AUTO_IP_FILE=${AUTO_CONF_DIR}/IPADDR
AUTO_HOSTNAME_FILE=${AUTO_CONF_DIR}/HOSTNAME
if [ ! -f ${AUTO_IP_FILE} ] ; then
  echo "dost not exists ${AUTO_IP_FILE}"
  exit 1
fi
if [ ! -f ${AUTO_HOSTNAME_FILE} ] ; then
  echo "dost not exists ${AUTO_HOSTNAME_FILE}"
  exit 1
fi

NEW_IP=$(cat ${AUTO_IP_FILE})
NEW_HOSTNAME=$(cat ${AUTO_HOSTNAME_FILE})

echo "AUTO_CONF_DIR=${AUTO_CONF_DIR}"
echo "NEW_IP=${NEW_IP}"
echo "NEW_HOSTNAME=${NEW_HOSTNAME}"

sed -i "s/192.168.181.128/${NEW_IP}/g" /etc/sysconfig/network-scripts/ifcfg-ens33
hostnamectl set-hostname ${NEW_HOSTNAME}
systemctl restart network

systemctl disable rc-local
systemctl stop    rc-local
sed -i 's/~\/set_auto.sh//g' /etc/rc.d/rc.local
chmod 644 /etc/rc.d/rc.local

ping -c 1 google.com
cat /etc/resolv.conf
```

# Usage

Windows 의 경우 다음 명령어 실행

```shell
java -jar E:\vm\CopyVMWare-0.5.jar `
 --autoConfPath E:\vm\conf `
 --sourcePath   E:\vm\linux `
 --sourceVMName template76 `
 --targetPath   F:\vm\linux `
 --targetVMNames nfsserver,nfsclient `
 --targetVMIPs   192.168.181.211,192.168.181.212 `
 --targetDomain  sky.local
```

*nix 의 경우 다음 명령어 실행

```shell
java -jar /path/to/CopyVMWare-0.5.jar \
 --autoConfPath /path/to/conf \
 --sourcePath   /path/to/linux \
 --sourceVMName template76 \
 --targetPath   /path/to/linux \
 --targetVMNames nfsserver,nfsclient \
 --targetVMIPs   192.168.181.211,192.168.181.212 \
 --targetDomain  sky.local
```

VM On
