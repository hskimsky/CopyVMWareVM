# VHD

Virtual Hard Disk

## image 생성

```shell
dd if=/dev/zero of=/root/VHD.img bs=1M count=32
ll
mkfs -t ext4 -F /root/VHD.img
ll
```

```text
[root@nas ~]# dd if=/dev/zero of=/root/VHD.img bs=1M count=32
32+0 records in
32+0 records out
33554432 bytes (34 MB) copied, 0.0191256 s, 1.8 GB/s
[root@nas ~]# ll
total 32780
-rw-------. 1 root root     1420 Jan 29 13:04 anaconda-ks.cfg
-rw-r--r--. 1 root root      308 Jan 29 15:08 ifcfg-eth0.bak
drwxrwxrwx. 3 root root       18 Feb 21 03:09 nfs
-rwxr-xr-x. 1 root root      360 Jan 29 15:09 set.sh
-rw-r--r--. 1 root root 33554432 Feb 21 15:44 VHD.img
[root@nas ~]# mkfs -t ext4 -F /root/VHD.img
mke2fs 1.42.9 (28-Dec-2013)
Discarding device blocks: done
Filesystem label=
OS type: Linux
Block size=1024 (log=0)
Fragment size=1024 (log=0)
Stride=0 blocks, Stripe width=0 blocks
8192 inodes, 32768 blocks
1638 blocks (5.00%) reserved for the super user
First data block=1
Maximum filesystem blocks=33554432
4 block groups
8192 blocks per group, 8192 fragments per group
2048 inodes per group
Superblock backups stored on blocks:
8193, 24577

Allocating group tables: done
Writing inode tables: done
Creating journal (4096 blocks): done
Writing superblocks and filesystem accounting information: done

[root@nas ~]# ll
total 4472
-rw-------. 1 root root     1420 Jan 29 13:04 anaconda-ks.cfg
-rw-r--r--. 1 root root      308 Jan 29 15:08 ifcfg-eth0.bak
drwxrwxrwx. 3 root root       18 Feb 21 03:09 nfs
-rwxr-xr-x. 1 root root      360 Jan 29 15:09 set.sh
-rw-r--r--. 1 root root 33554432 Feb 21 15:47 VHD.img
[root@nas ~]#
```

## mount

```shell
mkdir /root/VHD/
mount -t auto -o loop /root/VHD.img /root/VHD/
ll
ll VHD
ll VHD/lost+found/
```

```text
[root@nas ~]# mkdir /root/VHD/
[root@nas ~]# mount -t auto -o loop /root/VHD.img /root/VHD/
[root@nas ~]# ll
total 4473
-rw-------. 1 root root     1420 Jan 29 13:04 anaconda-ks.cfg
-rw-r--r--. 1 root root      308 Jan 29 15:08 ifcfg-eth0.bak
drwxrwxrwx. 3 root root       18 Feb 21 03:09 nfs
-rwxr-xr-x. 1 root root      360 Jan 29 15:09 set.sh
drwxr-xr-x. 3 root root     1024 Feb 21 15:47 VHD
-rw-r--r--. 1 root root 33554432 Feb 21 15:50 VHD.img
[root@nas ~]# ll VHD
total 12
drwx------. 2 root root 12288 Feb 21 15:47 lost+found
[root@nas ~]# ll VHD/lost+found/
total 0
[root@nas ~]#
```

## fstab

```shell
echo "/root/VHD.img /root/VHD/ ext4 defaults 0 0" >> /etc/fstab
```

## unmount

```shell
umount /root/VHD/
rm -f /root/VHD.img
```
