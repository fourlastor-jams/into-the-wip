<?xml version="1.0" encoding="UTF-8"?>
<tileset version="1.10" tiledversion="1.10.1" name="Monsters" tilewidth="32" tileheight="64" tilecount="4" columns="2" objectalignment="bottomleft">
 <tileoffset x="16" y="0"/>
 <image source="monsters.png" width="64" height="128"/>
 <tile id="0">
  <animation>
   <frame tileid="0" duration="250"/>
   <frame tileid="2" duration="250"/>
  </animation>
 </tile>
 <tile id="1">
  <animation>
   <frame tileid="1" duration="250"/>
   <frame tileid="3" duration="250"/>
  </animation>
 </tile>
 <tile id="2">
  <properties>
   <property name="enemy" type="bool" value="false"/>
   <property name="name" value="pink"/>
  </properties>
 </tile>
 <tile id="3">
  <properties>
   <property name="enemy" type="bool" value="true"/>
   <property name="name" value="green"/>
  </properties>
 </tile>
</tileset>
