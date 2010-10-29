rm -rf reaper
ant all
chown -R root:root reaper
chmod 775 reaper/DEBIAN
chmod 775 reaper/DEBIAN/*
chmod 755 reaper/etc
chmod 755 reaper/etc/init.d
chmod 755 reaper/etc/init.d/reaper
chmod 755 reaper/opt
chmod 770 reaper/opt/reaper
chmod 770 reaper/opt/reaper/*
chmod 550 reaper/opt/reaper/bin/bpf
chmod 550 reaper/opt/reaper/bin/filter.sh
chmod 770 reaper/opt/reaper/config/*
chmod 550 reaper/opt/reaper/lib/*
dpkg-deb -b reaper
