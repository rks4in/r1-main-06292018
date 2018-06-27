
/*
 * This file has been generated, do not modify this file!
 * Source file(s): ClientAPI/iDrivingContextInfo/iDrivingContextInfo.MetricDefs.xml,DataAccess/Common/DataAccess.MetricDefs.xml,Engines/FreeTextSearch/FreeTextSearch.MetricDefs.xml,Engines/MapInfo/MapInfo.MetricDefs.xml,Engines/MapMatching/MapMatching.MetricDefs.xml,Engines/MapManagement/MapUpdate/MapUpdate.MetricDefs.xml,Engines/MapVis/MapVis.MetricDefs.xml,Engines/Routing/Routing.MetricDefs.xml,Engines/System/EnginesSystem.MetricDefs.xml,Engines/Traffic/Traffic.MetricDefs.xml,Framework/Common/SQLiteUtils/SQLiteUtils.MetricDefs.xml,Framework/TestInfrastructure/PerfAgentController/Common/System.MetricDefs.xml
 * Generated on:   2018-03-06T16:26:20Z
 */

package com.tomtom.perfagentcontroller;

public final class MetricDefs {
    public static final class Checkpoints {
        // Empty
        public static final int MAPINFO_QUERY_BEGIN = 0x0cfcb004;

        // Map matcher went on road.
        public static final int MAPMATCHING_MAPMATCHER_JOINROAD = 0x451da023;

        // Map matcher went off road.
        public static final int MAPMATCHING_MAPMATCHER_LEAVEROAD = 0x451da024;

        // A U turn was detected.
        public static final int MAPMATCHING_MAPMATCHER_UTURNDETECTED = 0x451da025;

        // It was detected that a parking house was entered.
        public static final int MAPMATCHING_MAPMATCHER_ENTERPARKINGHOUSE = 0x451da026;

        // It was detected that a parking house was exited.
        public static final int MAPMATCHING_MAPMATCHER_EXITPARKINGHOUSE = 0x451da027;

        // Empty
        public static final int MAPUPDATE_COMMITPHASE = 0x34bb2005;

        // Reported when all tiles of a scene are rendered. Can be used to measure "cold boot" scenario.
        public static final int MAPVIS_SCENERENDERERSDK_ALLTILESARERENDERED = 0x0566b001;

        // Reported when the first frame is drawn.
        public static final int MAPVIS_SCENERENDERER_FIRSTFRAMEDRAWN = 0x0566b002;

        // Reported when SceneRenderer is destroyed.
        public static final int MAPVIS_SCENERENDERER_SCENERENDERERDESTROYED = 0x0566b003;

        // Reported after MapViewer2 configuration is sent to SceneBuilder via reflection interface. Can be used for synchronization.
        public static final int MAPVIS_MAPVIEWER2_PARAMETERSARESENTTOSCENEBUILDER = 0x0566b004;

        // Reported after camera behavior is set via MapViewer2 API (see description of "TiMapViewer2CameraBehavior" type for the details)
        public static final int MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2MANUALCAMERA3D = 0x0566b005;

        // Reported after camera behavior is set via MapViewer2 API (see description of "TiMapViewer2CameraBehavior" type for the details)
        public static final int MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2MANUALCAMERA2D = 0x0566b006;

        // Reported after camera behavior is set via MapViewer2 API (see description of "TiMapViewer2CameraBehavior" type for the details)
        public static final int MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE2DNORTHUPSHOWREMAININGROUTE = 0x0566b007;

        // Reported after camera behavior is set via MapViewer2 API (see description of "TiMapViewer2CameraBehavior" type for the details)
        public static final int MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE2DNORTHUPMANUALSCALE = 0x0566b008;

        // Reported after camera behavior is set via MapViewer2 API (see description of "TiMapViewer2CameraBehavior" type for the details)
        public static final int MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE2DDIRECTIONUPMANUALSCALE = 0x0566b009;

        // Reported after camera behavior is set via MapViewer2 API (see description of "TiMapViewer2CameraBehavior" type for the details)
        public static final int MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE2DDIRECTIONUPSCALEWITHSPEED = 0x0566b00a;

        // Reported after camera behavior is set via MapViewer2 API (see description of "TiMapViewer2CameraBehavior" type for the details)
        public static final int MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE2DDIRECTIONUPSCALETONEXTINSTRUCTION = 0x0566b00b;

        // Reported after camera behavior is set via MapViewer2 API (see description of "TiMapViewer2CameraBehavior" type for the details)
        public static final int MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE3DDIRECTIONUPMANUALSCALE = 0x0566b00c;

        // Reported after camera behavior is set via MapViewer2 API (see description of "TiMapViewer2CameraBehavior" type for the details)
        public static final int MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE3DDIRECTIONUPSCALEWITHSPEED = 0x0566b00d;

        // Reported after camera behavior is set via MapViewer2 API (see description of "TiMapViewer2CameraBehavior" type for the details)
        public static final int MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE3DDIRECTIONUPSCALETONEXTINSTRUCTION = 0x0566b00e;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERGROUNDFACES = 0x0566b00f;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERBUILDINGFOOTPRINTS = 0x0566b010;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERFERRYLINES = 0x0566b011;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERRAILWAYS = 0x0566b012;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERROADS = 0x0566b013;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERBUILDINGSACMLOD2 = 0x0566b014;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERLANDMARKS = 0x0566b015;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERACTIVEROUTE = 0x0566b016;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERPOIS = 0x0566b017;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERSAFETYCAMERAS = 0x0566b018;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERSTREETNAMES = 0x0566b019;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERNODEANDCITYNAMES = 0x0566b01a;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERTRAFFIC = 0x0566b01b;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERCURRENTPOSITION = 0x0566b01c;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERGROUNDFACES = 0x0566b01d;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERBUILDINGFOOTPRINTS = 0x0566b01e;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERFERRYLINES = 0x0566b01f;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERRAILWAYS = 0x0566b020;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERROADS = 0x0566b021;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERBUILDINGSACMLOD2 = 0x0566b022;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERLANDMARKS = 0x0566b023;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERACTIVEROUTE = 0x0566b024;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERPOIS = 0x0566b025;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERSAFETYCAMERAS = 0x0566b026;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERSTREETNAMES = 0x0566b027;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERNODEANDCITYNAMES = 0x0566b028;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERTRAFFIC = 0x0566b029;

        // Reported after a layer is set via MapViewer2 API (see description of "TiMapViewer2StockLayer" type for details).
        public static final int MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERCURRENTPOSITION = 0x0566b02a;

        // Checkpoint to silence clang warning for empty enum declarations
        public static final int ROUTING_DUMMYGROUP_DUMMYCHECKPOINT = 0x7d15d048;

        // Indicates that NavKitMain just started to run. It's the first checkpoint after initializing the instrumentation library
        public static final int ENGINES_SYSTEM_START = 0x64132003;

        // Indicates that NavKitMain is about to finish execution.
        public static final int ENGINES_SYSTEM_ABOUTTOEXIT = 0x64132004;

    }

    public static final class Counters {
        // Empty
        public static final int DATAACCESS_TTC_DATA_MEMORYUSED = 0x00d81000;

        // Empty
        public static final int DATAACCESS_NDS_POI_MAP = 0x00d81001;

        // Empty
        public static final int DATAACCESS_NDS_MAP_DTM_CACHEMEM = 0x00d81002;

        // Empty
        public static final int DATAACCESS_NDS_MAP_DTM_CACHEHITCOUNT = 0x00d81003;

        // Empty
        public static final int DATAACCESS_NDS_MAP_DTM_CACHEMISSCOUNT = 0x00d81004;

        // Empty
        public static final int DATAACCESS_NDS_MAP_BMD_CACHEMEM = 0x00d81005;

        // Empty
        public static final int DATAACCESS_NDS_MAP_BMD_CACHEHITCOUNT = 0x00d81006;

        // Empty
        public static final int DATAACCESS_NDS_MAP_BMD_CACHEMISSCOUNT = 0x00d81007;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_MAPSIZE = 0x00d81008;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_MAPCAPACITY = 0x00d81009;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_MAPBUCKETCOUNT = 0x00d8100a;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_MAPMAXLOADFACTOR = 0x00d8100b;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_MAPELEMENTSIZE = 0x00d8100c;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_HITCOUNT = 0x00d8100d;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_MISSCOUNT = 0x00d8100e;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_TMCLOCATIONTABLESMAPPING_SIZE = 0x00d8100f;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_TMCLOCATIONTABLESMAPPING_CAPACITY = 0x00d81010;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_MAPSIZE = 0x00d81011;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_MAPCAPACITY = 0x00d81012;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_MAPBUCKETCOUNT = 0x00d81013;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_MAPMAXLOADFACTOR = 0x00d81014;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_MAPELEMENTSIZE = 0x00d81015;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_HITCOUNT = 0x00d81016;

        // Empty
        public static final int DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_MISSCOUNT = 0x00d81017;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_MAPSIZE = 0x00d81018;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_MAPCAPACITY = 0x00d81019;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_MAPBUCKETCOUNT = 0x00d8101a;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_MAPMAXLOADFACTOR = 0x00d8101b;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_MAPELEMENTSIZE = 0x00d8101c;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_HITCOUNT = 0x00d8101d;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_MISSCOUNT = 0x00d8101e;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_MAPSIZE = 0x00d8101f;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_MAPCAPACITY = 0x00d81020;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_MAPBUCKETCOUNT = 0x00d81021;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_MAPMAXLOADFACTOR = 0x00d81022;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_MAPELEMENTSIZE = 0x00d81023;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_HITCOUNT = 0x00d81024;

        // Empty
        public static final int DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_MISSCOUNT = 0x00d81025;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_CACHE_MAPSIZE = 0x00d81026;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_CACHE_MAPCAPACITY = 0x00d81027;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_CACHE_MAPBUCKETCOUNT = 0x00d81028;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_CACHE_MAPMAXLOADFACTOR = 0x00d81029;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_CACHE_MAPELEMENTSIZE = 0x00d8102a;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_CACHE_HITCOUNT = 0x00d8102b;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_CACHE_MISSCOUNT = 0x00d8102c;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_KEYCACHE_MAPSIZE = 0x00d8102d;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_KEYCACHE_MAPCAPACITY = 0x00d8102e;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_KEYCACHE_MAPBUCKETCOUNT = 0x00d8102f;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_KEYCACHE_MAPMAXLOADFACTOR = 0x00d81030;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_KEYCACHE_MAPELEMENTSIZE = 0x00d81031;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_KEYCACHE_HITCOUNT = 0x00d81032;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_KEYCACHE_MISSCOUNT = 0x00d81033;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_MAPSIZE = 0x00d81034;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_MAPCAPACITY = 0x00d81035;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_MAPBUCKETCOUNT = 0x00d81036;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_MAPMAXLOADFACTOR = 0x00d81037;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_MAPELEMENTSIZE = 0x00d81038;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_HITCOUNT = 0x00d81039;

        // Empty
        public static final int DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_MISSCOUNT = 0x00d8103a;

        // Empty
        public static final int DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_MAPSIZE = 0x00d8103b;

        // Empty
        public static final int DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_MAPCAPACITY = 0x00d8103c;

        // Empty
        public static final int DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_MAPBUCKETCOUNT = 0x00d8103d;

        // Empty
        public static final int DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_MAPMAXLOADFACTOR = 0x00d8103e;

        // Empty
        public static final int DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_MAPELEMENTSIZE = 0x00d8103f;

        // Empty
        public static final int DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_HITCOUNT = 0x00d81040;

        // Empty
        public static final int DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_MISSCOUNT = 0x00d81041;

        // Empty
        public static final int DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_MAPSIZE = 0x00d81042;

        // Empty
        public static final int DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_MAPCAPACITY = 0x00d81043;

        // Empty
        public static final int DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_MAPBUCKETCOUNT = 0x00d81044;

        // Empty
        public static final int DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_MAPMAXLOADFACTOR = 0x00d81045;

        // Empty
        public static final int DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_MAPELEMENTSIZE = 0x00d81046;

        // Empty
        public static final int DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_HITCOUNT = 0x00d81047;

        // Empty
        public static final int DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_MISSCOUNT = 0x00d81048;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_MAPSIZE = 0x00d81049;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_MAPCAPACITY = 0x00d8104a;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_MAPBUCKETCOUNT = 0x00d8104b;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_MAPMAXLOADFACTOR = 0x00d8104c;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_MAPELEMENTSIZE = 0x00d8104d;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_HITCOUNT = 0x00d8104e;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_MISSCOUNT = 0x00d8104f;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_MAPSIZE = 0x00d81050;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_MAPCAPACITY = 0x00d81051;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_MAPBUCKETCOUNT = 0x00d81052;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_MAPMAXLOADFACTOR = 0x00d81053;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_MAPELEMENTSIZE = 0x00d81054;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_HITCOUNT = 0x00d81055;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_MISSCOUNT = 0x00d81056;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_MAPSIZE = 0x00d81057;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_MAPCAPACITY = 0x00d81058;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_MAPBUCKETCOUNT = 0x00d81059;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_MAPMAXLOADFACTOR = 0x00d8105a;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_MAPELEMENTSIZE = 0x00d8105b;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_HITCOUNT = 0x00d8105c;

        // Empty
        public static final int DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_MISSCOUNT = 0x00d8105d;

        // Empty
        public static final int DATAACCESS_NDS_NAMESRING_CACHE_MAPSIZE = 0x00d8105e;

        // Empty
        public static final int DATAACCESS_NDS_NAMESRING_CACHE_MAPCAPACITY = 0x00d8105f;

        // Empty
        public static final int DATAACCESS_NDS_NAMESRING_CACHE_MAPBUCKETCOUNT = 0x00d81060;

        // Empty
        public static final int DATAACCESS_NDS_NAMESRING_CACHE_MAPMAXLOADFACTOR = 0x00d81061;

        // Empty
        public static final int DATAACCESS_NDS_NAMESRING_CACHE_MAPELEMENTSIZE = 0x00d81062;

        // Empty
        public static final int DATAACCESS_NDS_NAMESRING_CACHE_HITCOUNT = 0x00d81063;

        // Empty
        public static final int DATAACCESS_NDS_NAMESRING_CACHE_MISSCOUNT = 0x00d81064;

        // Empty
        public static final int DATAACCESS_NDS_ADASRING_TILECACHE_MAPSIZE = 0x00d81065;

        // Empty
        public static final int DATAACCESS_NDS_ADASRING_TILECACHE_MAPCAPACITY = 0x00d81066;

        // Empty
        public static final int DATAACCESS_NDS_ADASRING_TILECACHE_MAPBUCKETCOUNT = 0x00d81067;

        // Empty
        public static final int DATAACCESS_NDS_ADASRING_TILECACHE_MAPMAXLOADFACTOR = 0x00d81068;

        // Empty
        public static final int DATAACCESS_NDS_ADASRING_TILECACHE_MAPELEMENTSIZE = 0x00d81069;

        // Empty
        public static final int DATAACCESS_NDS_ADASRING_TILECACHE_HITCOUNT = 0x00d8106a;

        // Empty
        public static final int DATAACCESS_NDS_ADASRING_TILECACHE_MISSCOUNT = 0x00d8106b;

        // Empty
        public static final int DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_MAPSIZE = 0x00d8106c;

        // Empty
        public static final int DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_MAPCAPACITY = 0x00d8106d;

        // Empty
        public static final int DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_MAPBUCKETCOUNT = 0x00d8106e;

        // Empty
        public static final int DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_MAPMAXLOADFACTOR = 0x00d8106f;

        // Empty
        public static final int DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_MAPELEMENTSIZE = 0x00d81070;

        // Empty
        public static final int DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_HITCOUNT = 0x00d81071;

        // Empty
        public static final int DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_MISSCOUNT = 0x00d81072;

        // Empty
        public static final int DATAACCESS_NDS_MAPACCESSBASERING_MAP = 0x00d81073;

        // Empty
        public static final int DATAACCESS_NDS_MAPACCESSBASERING_PRODUCTS = 0x00d81074;

        // Empty
        public static final int DATAACCESS_NDS_MAPACCESSBASERING_UPDATEREGIONS = 0x00d81075;

        // Empty
        public static final int DATAACCESS_NDS_MAPACCESSBASERING_METADATAREGIONS = 0x00d81076;

        // Empty
        public static final int DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_MAPSIZE = 0x00d81077;

        // Empty
        public static final int DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_MAPCAPACITY = 0x00d81078;

        // Empty
        public static final int DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_MAPBUCKETCOUNT = 0x00d81079;

        // Empty
        public static final int DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_MAPMAXLOADFACTOR = 0x00d8107a;

        // Empty
        public static final int DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_MAPELEMENTSIZE = 0x00d8107b;

        // Empty
        public static final int DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_HITCOUNT = 0x00d8107c;

        // Empty
        public static final int DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_MISSCOUNT = 0x00d8107d;

        // Empty
        public static final int FREETEXTSEARCH_COMBOSEARCHENGINE_MEMORYUSAGE = 0x1d135004;

        // Empty
        public static final int MAPINFO_QUERY_COUNT = 0x0cfcb003;

        // Processing delay between GNSS input and Position Engine output.
        public static final int MAPMATCHING_POSITIONUPDATE_DELAYS_GNSSTOPE = 0x451da014;

        // Processing delay between Position Engine Output and Map Matching input.
        public static final int MAPMATCHING_POSITIONUPDATE_DELAYS_PETOMMPOS = 0x451da015;

        // Processing delay between GNSS input and Map Matching input.
        public static final int MAPMATCHING_POSITIONUPDATE_DELAYS_GNSSTOMMPOS = 0x451da016;

        // Number of paths in the Path Tree.
        public static final int MAPMATCHING_PATHTREE_PATHS = 0x451da017;

        // Number of match roads in the Road Cache.
        public static final int MAPMATCHING_ROADCACHE_MATCHROADS = 0x451da018;

        // Number of POIs in the stable pool.
        public static final int MAPMATCHING_POIWARNING_POISINSTABLEPOOL = 0x451da019;

        // Estimated memory usage of the map matcher in bytes.
        public static final int MAPMATCHING_MAPMATCHER_MEMORYUSAGE = 0x451da01a;

        // Number of safety locations in the stable pool.
        public static final int MAPMATCHING_SAFETYLOCATIONS_INSTABLEPOOL = 0x451da01b;

        // Reports 1 every time the path is reset. To be summed in tests.
        public static final int MAPMATCHING_ADAS_RESETPATHCOUNT = 0x451da01c;

        // Reports 1 every thime the path is updated. To be summed in tests.
        public static final int MAPMATCHING_ADAS_UPDATEPATHCOUNT = 0x451da01d;

        // Total number of bytes received of protobuf messages
        public static final int MAPMATCHING_ROUTEDATAPROVIDER_BYTESRECEIVED = 0x451da01e;

        // Total number of bytes received of protobuf messages
        public static final int MAPMATCHING_MAPDATAPROVIDER_BYTESRECEIVED = 0x451da01f;

        // Total number of bytes received of protobuf messages
        public static final int MAPMATCHING_RESULTPROVIDER_BYTESRECEIVED = 0x451da020;

        // Mapmatcher full state size.
        public static final int MAPMATCHING_PATHMATCHER_FULLSTATESIZE = 0x451da021;

        // Map data cache size.
        public static final int MAPMATCHING_PATHMATCHER_MAPDATACACHESIZE = 0x451da022;

        // Empty
        public static final int MAPUPDATE_UPDATEPROGRESS = 0x34bb2003;

        // Empty
        public static final int MAPUPDATE_VALIDATIONPROGRESS = 0x34bb2004;

        // Counts how many times CInstructionBuilder::BuildLegacyInstructionsCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_BUILDLEGACYINSTRUCTIONSCOUNTER = 0x7d15d02a;

        // Counts how many times CInstructionBuilder::BuildLaneInformationCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_BUILDLANEINFORMATIONCOUNTER = 0x7d15d02b;

        // Counts how many times CInstructionBuilder::CheckForBifurcationCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_CHECKFORBIFURCATIONCOUNTER = 0x7d15d02c;

        // Counts how many times CInstructionBuilder::InvestigateChangeCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_INVESTIGATECHANGECOUNTER = 0x7d15d02d;

        // Counts how many times CInstructionBuilder::CreateLaneGuidanceInstructionCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_CREATELANEGUIDANCEINSTRUCTIONCOUNTER = 0x7d15d02e;

        // Counts how many times CInstructionBuilder::MotorwayBifurcationOrExitCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_MOTORWAYBIFURCATIONOREXITCOUNTER = 0x7d15d02f;

        // Counts how many times CInstructionBuilder::BifurcationAtPosCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_BIFURCATIONATPOSCOUNTER = 0x7d15d030;

        // Counts how many times CInstructionBuilder::BuildLaneInfoAndAppendInstructionCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_BUILDLANEINFOANDAPPENDINSTRUCTIONCOUNTER = 0x7d15d031;

        // Counts how many times CInstructionBuilder::GenerateBifurcationInstructionCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_GENERATEBIFURCATIONINSTRUCTIONCOUNTER = 0x7d15d032;

        // Counts how many times CInstructionBuilder::GenerateInstructionCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_GENERATEINSTRUCTIONCOUNTER = 0x7d15d033;

        // Counts how many times CIntersectionBuilder::GatherOutgoingLinesCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_GATHEROUTGOINGLINESCOUNTER = 0x7d15d034;

        // Counts how many times CInstructionBuilder::FillLanesInfoCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_FILLLANESINFOCOUNTER = 0x7d15d035;

        // Counts how many times CInstructionBuilder::ConnectivityMaxDepthOnUpcomingArcsCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_CONNECTIVITYMAXDEPTHONUPCOMINGARCSCOUNTER = 0x7d15d036;

        // Counts how many times CInstructionBuilder::LaneMaxDepthOnUpcomingArcsCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_LANEMAXDEPTHONUPCOMINGARCSCOUNTER = 0x7d15d037;

        // Counts how many times CInstructionBuilder::GetEndLanesCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETENDLANESCOUNTER = 0x7d15d038;

        // Counts how many times CInstructionBuilder::GetEndLanesIteratorCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETENDLANESITERATORCOUNTER = 0x7d15d039;

        // Counts how many times CInstructionBuilder::ReorderLanesCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_REORDERLANESCOUNTER = 0x7d15d03a;

        // Counts how many times CInstructionBuilder::GetNumberOfLanesInDrivingDirectionCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETNUMBEROFLANESINDRIVINGDIRECTIONCOUNTER = 0x7d15d03b;

        // Counts how many times CInstructionBuilder::GetLaneIndexCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETLANEINDEXCOUNTER = 0x7d15d03c;

        // Counts how many times CInstructionBuilder::GetLaneMarkingCounter() was called.
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETLANEMARKINGCOUNTER = 0x7d15d03d;

        // Empty
        public static final int ROUTING_GUIDANCE_ROUTEINFO_ARCCOUNTER = 0x7d15d03e;

        // Memory consumption of the routing engine (without map caches)
        public static final int ROUTING_ROUTINGENGINE_MEMORYCONSUMPTION = 0x7d15d03f;

        // Number of pops performed during route computation
        public static final int ROUTING_ROUTINGENGINE_POPCOUNT = 0x7d15d040;

        // Maximum number of active labels in the routing engine
        public static final int ROUTING_ROUTINGENGINE_MAXLABELS = 0x7d15d041;

        // The length of the current route in meters
        public static final int ROUTING_ROUTINGENGINE_ROUTELENGTH = 0x7d15d042;

        // The number of arcs processed in the ArcStorageJob
        public static final int ROUTING_PERSONALNETWORK_ARCSTORAGEJOBARCLISTSIZE = 0x7d15d043;

        // Number of sqlite3_step invocations
        public static final int ROUTING_PERSONALNETWORK_SQLITESTEPS = 0x7d15d044;

        // Number of sqlite3_exec invocations
        public static final int ROUTING_PERSONALNETWORK_SQLITEEXECS = 0x7d15d045;

        // Number of detected stretches in the current StoreArcList invocation
        public static final int ROUTING_PERSONALNETWORK_TRIPSTRETCHES = 0x7d15d046;

        // Number of SplitStrech invocations on the database controller
        public static final int ROUTING_PERSONALNETWORK_SPLITSTRETCH = 0x7d15d047;

        // Report static and dynamic memory usage of the traffic table
        public static final int TRAFFIC_TRAFFICTABLE_MEMORYUSEDKIB = 0x3d139006;

        // Empty
        public static final int SQLITEUTILS_MEMORYUSED = 0x12a86000;

        // Empty
        public static final int SQLITEUTILS_MEMORYHIGHWATER = 0x12a86001;

        // Special metric used internally in the PAC to signal that the metric using this ID has its name defined in the test (JUnit) and that no conversion from ID to string is necessary
        public static final int SYSTEM_TEST_INTERNAL = 0x3c079000;

        // Memory usage
        public static final int SYSTEM_MEMORY_USAGE = 0x3c079001;

        // Memory Resident Set Size
        public static final int SYSTEM_MEMORY_RSS = 0x3c079002;

        // Memory Proportional Set Size
        public static final int SYSTEM_MEMORY_PSS = 0x3c079003;

        // Shared Memory
        public static final int SYSTEM_MEMORY_SHARED = 0x3c079004;

        // CPU Usage
        public static final int SYSTEM_CPU_USAGE = 0x3c079005;

        // Open Files
        public static final int SYSTEM_IO_OPENFILES = 0x3c079006;

        // Read Count
        public static final int SYSTEM_IO_READCOUNT = 0x3c079007;

        // Write Count
        public static final int SYSTEM_IO_WRITECOUNT = 0x3c079008;

        // Bytes Read
        public static final int SYSTEM_IO_READBYTES = 0x3c079009;

        // Bytes Written
        public static final int SYSTEM_IO_WRITEBYTES = 0x3c07900a;

    }

    public static final class Timers {
        // Time spent parsing a Query and sending the (initial) response.
        public static final int IDRIVINGCONTEXTINFO_QUERY = 0x34674000;

        // Time spent parsing a QuerySet and sending the (initial) response.
        public static final int IDRIVINGCONTEXTINFO_QUERYSET = 0x34674001;

        // Time spent removing a running query.
        public static final int IDRIVINGCONTEXTINFO_CLOSEQUERY = 0x34674002;

        // Time spent parsing the query.
        public static final int IDRIVINGCONTEXTINFO_PARSE = 0x34674003;

        // Time spent collecting and filtering query results.
        public static final int IDRIVINGCONTEXTINFO_EXECUTE = 0x34674004;

        // Time spent packing and sending the results.
        public static final int IDRIVINGCONTEXTINFO_SENDRESULT = 0x34674005;

        // Time spent packing results for transfer over Reflection.
        public static final int IDRIVINGCONTEXTINFO_RESULTPACKER = 0x34674006;

        // Empty
        public static final int FREETEXTSEARCH_COMBOSEARCHENGINE_QUERY_EXECUTION_DURATION = 0x1d135000;

        // Empty
        public static final int FREETEXTSEARCH_COMBOSEARCHENGINE_QUERY_EXECUTION_AVERAGE = 0x1d135001;

        // Empty
        public static final int FREETEXTSEARCH_COMBOSEARCHENGINE_QUERY_EXECUTION_MEDIAN = 0x1d135002;

        // Empty
        public static final int FREETEXTSEARCH_COMBOSEARCHENGINE_QUERY_EXECUTION_MAX = 0x1d135003;

        // Empty
        public static final int MAPINFO_ACTIVEOBJECT_SINGLERUNTIME = 0x0cfcb000;

        // Empty
        public static final int MAPINFO_QUERY_DURATION = 0x0cfcb001;

        // Empty
        public static final int MAPINFO_REFLECTIONDISPATCH_QUERY_DURATION = 0x0cfcb002;

        // Complete time spent processing a sample of incoming position data.
        public static final int MAPMATCHING_POSITIONUPDATE_PROCESSREALPOSITIONUPDATE = 0x451da000;

        // Time spent in the map matcher when notified of a new sample of position data.
        public static final int MAPMATCHING_POSITIONUPDATE_OBSERVERS_MAPMATCHER = 0x451da001;

        // Time spent notifying other observers of a new sample if incoming position data.
        public static final int MAPMATCHING_POSITIONUPDATE_OBSERVERS_OTHER = 0x451da002;

        // Total time spent updating POI results. Includes search in external components.
        public static final int MAPMATCHING_POIWARNING_UPDATE = 0x451da003;

        // Time spent searching for POIs in external component.
        public static final int MAPMATCHING_POIWARNING_SEARCH = 0x451da004;

        // Time spent running the Position Simulation controller.
        public static final int MAPMATCHING_POSITIONSIMULATION_RUN = 0x451da005;

        // Time spent generating simulated position data.
        public static final int MAPMATCHING_POSITIONSIMULATION_GENERATEINPUT = 0x451da006;

        // Time spent generating next position on route demo.
        public static final int MAPMATCHING_POSITIONSIMULATION_GENERATEROUTEDEMO = 0x451da007;

        // Time spent updating the map matcher and other system state using simulated data.
        public static final int MAPMATCHING_POSITIONSIMULATION_POSITIONUPDATE = 0x451da008;

        // Time spent running the safety location controller.
        public static final int MAPMATCHING_SAFETYLOCATIONS_RUN = 0x451da009;

        // Time spent searching for safety locations and updating the source rectangle.
        public static final int MAPMATCHING_SAFETYLOCATIONS_UPDATE = 0x451da00a;

        // Time spent filtering safety locations according to horizon and route.
        public static final int MAPMATCHING_SAFETYLOCATIONS_FILTER = 0x451da00b;

        // Time spent syncing filtered safety locations into the stable pool.
        public static final int MAPMATCHING_SAFETYLOCATIONS_SYNC = 0x451da00c;

        // Time spent building a completely new ADAS path.
        public static final int MAPMATCHING_ADAS_RESETPATH = 0x451da00d;

        // Time spent updating the existing ADAS path.
        public static final int MAPMATCHING_ADAS_UPDATEPATH = 0x451da00e;

        // Time spent during map-matching position update procedure.
        public static final int MAPMATCHING_PATHMATCHER_ONPOSITIONUPDATE = 0x451da00f;

        // Time spent during path matching.
        public static final int MAPMATCHING_PATHMATCHER_PATHMATCHINGSTEP = 0x451da010;

        // Time spent during arc matching.
        public static final int MAPMATCHING_PATHMATCHER_ARCMATCHINGSTEP = 0x451da011;

        // Time spent to produce path-matching result.
        public static final int MAPMATCHING_PATHMATCHER_RESULTPATHMATCHING = 0x451da012;

        // Time spent in SubMap function without preselected segment keys (during arc cache update).
        public static final int MAPMATCHING_PATHMATCHER_SUBMAPTIMER = 0x451da013;

        // Empty
        public static final int MAPUPDATE_COMMITPHASE = 0x34bb2000;

        // Empty
        public static final int MAPUPDATE_VALIDATIONPHASE = 0x34bb2001;

        // Empty
        public static final int MAPUPDATE_ACTIVEOBJECT_SINGLERUNTIME = 0x34bb2002;

        // Started at the first OnDrawnFrame call, stopped and reported after 100 frames drawn.
        public static final int MAPVIS_SCENERENDERER_PERIODICALFRAMETIMER = 0x0566b000;

        // Empty
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_BUILDLEGACYINSTRUCTIONS = 0x7d15d000;

        // Empty
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_BUILDLANEINFORMATION = 0x7d15d001;

        // Empty
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_BUILDNAVKITINSTRUCTIONS = 0x7d15d002;

        // Empty
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_POPULATEPOLYLINE = 0x7d15d003;

        // Measures time spent in CInstructionBuilder::CheckForBifurcation().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_CHECKFORBIFURCATION = 0x7d15d004;

        // Measures time spent in CInstructionBuilder::InvestigateChange().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_INVESTIGATECHANGE = 0x7d15d005;

        // Measures time spent in CInstructionBuilder::CreateLaneGuidanceInstruction().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_CREATELANEGUIDANCEINSTRUCTION = 0x7d15d006;

        // Measures time spent in CInstructionBuilder::MotorwayBifurcationOrExit().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_MOTORWAYBIFURCATIONOREXIT = 0x7d15d007;

        // Measures time spent in CInstructionBuilder::BifurcationAtPos()
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_BIFURCATIONATPOS = 0x7d15d008;

        // Measures time spent in CInstructionBuilder::BuildLaneInfoAndAppendInstruction().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_BUILDLANEINFOANDAPPENDINSTRUCTION = 0x7d15d009;

        // Measures time spent in CInstructionBuilder::GenerateBifurcationInstruction().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_GENERATEBIFURCATIONINSTRUCTION = 0x7d15d00a;

        // Measures time spent in CInstructionBuilder::GenerateInstruction().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_GENERATEINSTRUCTION = 0x7d15d00b;

        // Measures time spent in CInstrumentationBuilder::GatherOutgoingLines().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_GATHEROUTGOINGLINES = 0x7d15d00c;

        // Measures time spent in NInstructionBuilderUtils::FillLanesInfo().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_FILLLANESINFO = 0x7d15d00d;

        // Measures time spent in NInstructionBuilderUtils::ConnectivityMaxDepthOnUpcomingArcs().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_CONNECTIVITYMAXDEPTHONUPCOMINGARCS = 0x7d15d00e;

        // Measures time spent in NInstructionBuilderUtils::LaneMaxDepthOnUpcomingArcs().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_LANEMAXDEPTHONUPCOMINGARCS = 0x7d15d00f;

        // Measures time spent in NInstructionBuilderUtils::GetEndLanes().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETENDLANES = 0x7d15d010;

        // Measures time spent in NInstructionBuilderUtils::GetEndLanesIterator().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETENDLANESITERATOR = 0x7d15d011;

        // Measures time spent in NInstructionBuilderUtils::ReorderLanes().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_REORDERLANES = 0x7d15d012;

        // Measures time spent in NInstructionBuilderUtils::GetNumberOfLanesInDrivingDirection().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETNUMBEROFLANESINDRIVINGDIRECTION = 0x7d15d013;

        // Measures time spent in NInstructionBuilderUtils::GetLaneIndex().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETLANEINDEX = 0x7d15d014;

        // Measures time spent in NInstructionBuilderUtils::GetLaneMarking().
        public static final int ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETLANEMARKING = 0x7d15d015;

        // Empty
        public static final int ROUTING_GUIDANCE_ROUTEINFO_ROUTEINFOSIGNPOSTSCOMPUTATION = 0x7d15d016;

        // Empty
        public static final int ROUTING_GUIDANCE_ROUTEINFO_ANALYZECOMPLEXMANEUVERS = 0x7d15d017;

        // Empty
        public static final int ROUTING_GUIDANCE_ROUTEINFO_GETSIGNPOSTS = 0x7d15d018;

        // Empty
        public static final int ROUTING_GUIDANCE_ROUTEINFO_CHECKROADNAMEORNUMBERCHANGES = 0x7d15d019;

        // Empty
        public static final int ROUTING_GUIDANCE_ROUTEINFO_GETROADNAMES = 0x7d15d01a;

        // Empty
        public static final int ROUTING_GUIDANCE_ROUTEINFO_FINDCONNECTINGROADS = 0x7d15d01b;

        // Empty
        public static final int ROUTING_GUIDANCE_ROUTE_ROUTEACTIVATION = 0x7d15d01c;

        // Timer for iRoute.Release(), the reflection interface method to release location handles
        public static final int ROUTING_GUIDANCE_ROUTE_RELEASE = 0x7d15d01d;

        // Empty
        public static final int ROUTING_GUIDANCE_SIDEROADS_SIDEROADSCOMPUTATION = 0x7d15d01e;

        // Timer for initial time slices taken by agent (wall clock time)
        public static final int ROUTING_ROUTINGSCHEDULER_x_INITIALTIMESLICEDURATION = 0x7d15d01f;

        // Timer for time slices taken by agent (wall clock time)
        public static final int ROUTING_ROUTINGSCHEDULER_x_TIMESLICEDURATION = 0x7d15d020;

        // Timer for pure route computation (wall clock time)
        public static final int ROUTING_ROUTINGENGINE_ROUTECOMPUTATION = 0x7d15d021;

        // Duration of the route computation (wall clock time)
        public static final int ROUTING_ROUTINGENGINE_ROUTECOMPUTATIONDURATION = 0x7d15d022;

        // Duration of the reachability search (wall clock time)
        public static final int ROUTING_ROUTINGENGINE_REACHABILITYSEARCHDURATION = 0x7d15d023;

        // Time spent resolving locations prior to route search
        public static final int ROUTING_ROUTINGENGINE_LOCATIONRESOLUTION = 0x7d15d024;

        // Time spent filling in the routing engine's input parameters
        public static final int ROUTING_ROUTINGENGINE_CREATETASK = 0x7d15d025;

        // Time spent in routing engine while submitting a routing task
        public static final int ROUTING_ROUTINGENGINE_SUBMITTASK = 0x7d15d026;

        // Time spent in routing engine in first Compute() call for a route
        public static final int ROUTING_ROUTINGENGINE_FIRSTCOMPUTECALL = 0x7d15d027;

        // The time spent during one ArcStorageJob
        public static final int ROUTING_PERSONALNETWORK_ARCSTORAGEJOBDURATION = 0x7d15d028;

        // Time spent in the active object for the Personal Network
        public static final int ROUTING_PERSONALNETWORK_ACTIVEOBJECT = 0x7d15d029;

        // Time it takes for the engines to respond to a map update notification, when the content changes
        public static final int ENGINES_SYSTEM_ONMAPUPDATED = 0x64132000;

        // Empty
        public static final int ENGINES_SYSTEM_MAPUPDATE_UNLOADMAP = 0x64132001;

        // Empty
        public static final int ENGINES_SYSTEM_MAPUPDATE_RELOADMAP = 0x64132002;

        // Empty
        public static final int TRAFFIC_DECODING_TRAFFICFETCHER_UPDATETRAFFICCACHEWITHEVENT_DECODE = 0x3d139000;

        // Empty
        public static final int TRAFFIC_DECODING_TTLRTRANSFORMER_DECODELOCATION = 0x3d139001;

        // Empty
        public static final int TRAFFIC_DECODING_TMCTRANSFORMER_DECODELOCATION = 0x3d139002;

        // Retrieving traffic events in a given area (CPU time)
        public static final int TRAFFIC_TRAFFICINHORIZON_CPU = 0x3d139003;

        // Retrieving traffic events in a given area (wall time)
        public static final int TRAFFIC_TRAFFICINHORIZON_WALL = 0x3d139004;

        // Loading and initializing TMC tables from the NDS map
        public static final int TRAFFIC_TMC_LOADTABLES = 0x3d139005;

    }


    public static final class MetricInformationProvider extends IMetricInformationProvider {
        private static final MetricInformations COUNTERS = new MetricInformations();
        private static final MetricInformations TIMERS = new MetricInformations();
        private static final MetricInformations CHECKPOINTS = new MetricInformations();

        static {
            CHECKPOINTS.add(new MetricInformation("MapInfo.Query.Begin", Checkpoints.MAPINFO_QUERY_BEGIN, 0));
            CHECKPOINTS.add(new MetricInformation("MapMatching.MapMatcher.JoinRoad", Checkpoints.MAPMATCHING_MAPMATCHER_JOINROAD, 0));
            CHECKPOINTS.add(new MetricInformation("MapMatching.MapMatcher.LeaveRoad", Checkpoints.MAPMATCHING_MAPMATCHER_LEAVEROAD, 0));
            CHECKPOINTS.add(new MetricInformation("MapMatching.MapMatcher.UTurnDetected", Checkpoints.MAPMATCHING_MAPMATCHER_UTURNDETECTED, 0));
            CHECKPOINTS.add(new MetricInformation("MapMatching.MapMatcher.EnterParkingHouse", Checkpoints.MAPMATCHING_MAPMATCHER_ENTERPARKINGHOUSE, 0));
            CHECKPOINTS.add(new MetricInformation("MapMatching.MapMatcher.ExitParkingHouse", Checkpoints.MAPMATCHING_MAPMATCHER_EXITPARKINGHOUSE, 0));
            CHECKPOINTS.add(new MetricInformation("MapUpdate.CommitPhase", Checkpoints.MAPUPDATE_COMMITPHASE, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.SceneRendererSdk.AllTilesAreRendered", Checkpoints.MAPVIS_SCENERENDERERSDK_ALLTILESARERENDERED, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.SceneRenderer.FirstFrameDrawn", Checkpoints.MAPVIS_SCENERENDERER_FIRSTFRAMEDRAWN, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.SceneRenderer.SceneRendererDestroyed", Checkpoints.MAPVIS_SCENERENDERER_SCENERENDERERDESTROYED, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.ParametersAreSentToSceneBuilder", Checkpoints.MAPVIS_MAPVIEWER2_PARAMETERSARESENTTOSCENEBUILDER, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.CameraBehavior.EiMapViewer2ManualCamera3D", Checkpoints.MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2MANUALCAMERA3D, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.CameraBehavior.EiMapViewer2ManualCamera2D", Checkpoints.MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2MANUALCAMERA2D, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.CameraBehavior.EiMapViewer2FollowVehicle2DNorthUpShowRemainingRoute", Checkpoints.MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE2DNORTHUPSHOWREMAININGROUTE, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.CameraBehavior.EiMapViewer2FollowVehicle2DNorthUpManualScale", Checkpoints.MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE2DNORTHUPMANUALSCALE, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.CameraBehavior.EiMapViewer2FollowVehicle2DDirectionUpManualScale", Checkpoints.MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE2DDIRECTIONUPMANUALSCALE, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.CameraBehavior.EiMapViewer2FollowVehicle2DDirectionUpScaleWithSpeed", Checkpoints.MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE2DDIRECTIONUPSCALEWITHSPEED, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.CameraBehavior.EiMapViewer2FollowVehicle2DDirectionUpScaleToNextInstruction", Checkpoints.MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE2DDIRECTIONUPSCALETONEXTINSTRUCTION, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.CameraBehavior.EiMapViewer2FollowVehicle3DDirectionUpManualScale", Checkpoints.MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE3DDIRECTIONUPMANUALSCALE, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.CameraBehavior.EiMapViewer2FollowVehicle3DDirectionUpScaleWithSpeed", Checkpoints.MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE3DDIRECTIONUPSCALEWITHSPEED, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.CameraBehavior.EiMapViewer2FollowVehicle3DDirectionUpScaleToNextInstruction", Checkpoints.MAPVIS_MAPVIEWER2_CAMERABEHAVIOR_EIMAPVIEWER2FOLLOWVEHICLE3DDIRECTIONUPSCALETONEXTINSTRUCTION, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerGroundFaces", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERGROUNDFACES, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerBuildingFootprints", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERBUILDINGFOOTPRINTS, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerFerryLines", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERFERRYLINES, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerRailways", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERRAILWAYS, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerRoads", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERROADS, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerBuildingsACMLod2", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERBUILDINGSACMLOD2, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerLandmarks", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERLANDMARKS, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerActiveRoute", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERACTIVEROUTE, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerPois", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERPOIS, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerSafetyCameras", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERSAFETYCAMERAS, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerStreetNames", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERSTREETNAMES, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerNodeAndCityNames", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERNODEANDCITYNAMES, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerTraffic", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERTRAFFIC, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersEnabled.EiMapViewer2LayerCurrentPosition", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSENABLED_EIMAPVIEWER2LAYERCURRENTPOSITION, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerGroundFaces", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERGROUNDFACES, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerBuildingFootprints", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERBUILDINGFOOTPRINTS, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerFerryLines", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERFERRYLINES, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerRailways", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERRAILWAYS, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerRoads", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERROADS, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerBuildingsACMLod2", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERBUILDINGSACMLOD2, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerLandmarks", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERLANDMARKS, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerActiveRoute", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERACTIVEROUTE, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerPois", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERPOIS, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerSafetyCameras", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERSAFETYCAMERAS, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerStreetNames", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERSTREETNAMES, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerNodeAndCityNames", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERNODEANDCITYNAMES, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerTraffic", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERTRAFFIC, 0));
            CHECKPOINTS.add(new MetricInformation("MapVis.MapViewer2.LayersDisabled.EiMapViewer2LayerCurrentPosition", Checkpoints.MAPVIS_MAPVIEWER2_LAYERSDISABLED_EIMAPVIEWER2LAYERCURRENTPOSITION, 0));
            CHECKPOINTS.add(new MetricInformation("Routing.DummyGroup.DummyCheckPoint", Checkpoints.ROUTING_DUMMYGROUP_DUMMYCHECKPOINT, 0));
            CHECKPOINTS.add(new MetricInformation("Engines.System.Start", Checkpoints.ENGINES_SYSTEM_START, 0));
            CHECKPOINTS.add(new MetricInformation("Engines.System.AboutToExit", Checkpoints.ENGINES_SYSTEM_ABOUTTOEXIT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.TTC.Data.MemoryUsed", Counters.DATAACCESS_TTC_DATA_MEMORYUSED, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.POI.Map", Counters.DATAACCESS_NDS_POI_MAP, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Map.DTM.CacheMem", Counters.DATAACCESS_NDS_MAP_DTM_CACHEMEM, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Map.DTM.CacheHitCount", Counters.DATAACCESS_NDS_MAP_DTM_CACHEHITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Map.DTM.CacheMissCount", Counters.DATAACCESS_NDS_MAP_DTM_CACHEMISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Map.BMD.CacheMem", Counters.DATAACCESS_NDS_MAP_BMD_CACHEMEM, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Map.BMD.CacheHitCount", Counters.DATAACCESS_NDS_MAP_BMD_CACHEHITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Map.BMD.CacheMissCount", Counters.DATAACCESS_NDS_MAP_BMD_CACHEMISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.LocationCache.MapSize", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.LocationCache.MapCapacity", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.LocationCache.MapBucketCount", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.LocationCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.LocationCache.MapElementSize", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.LocationCache.HitCount", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.LocationCache.MissCount", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_LOCATIONCACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.TMCLocationTablesMapping.Size", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_TMCLOCATIONTABLESMAPPING_SIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.TMCLocationTablesMapping.Capacity", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_TMCLOCATIONTABLESMAPPING_CAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.CodeMapCache.MapSize", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.CodeMapCache.MapCapacity", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.CodeMapCache.MapBucketCount", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.CodeMapCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.CodeMapCache.MapElementSize", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.CodeMapCache.HitCount", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.TMCLocationsRing.CodeMapCache.MissCount", Counters.DATAACCESS_NDS_TMCLOCATIONSRING_CODEMAPCACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.TileCache.MapSize", Counters.DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.TileCache.MapCapacity", Counters.DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.TileCache.MapBucketCount", Counters.DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.TileCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.TileCache.MapElementSize", Counters.DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.TileCache.HitCount", Counters.DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.TileCache.MissCount", Counters.DATAACCESS_NDS_ROUTINGDATARING_TILECACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.GatewayCache.MapSize", Counters.DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.GatewayCache.MapCapacity", Counters.DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.GatewayCache.MapBucketCount", Counters.DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.GatewayCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.GatewayCache.MapElementSize", Counters.DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.GatewayCache.HitCount", Counters.DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.RoutingDataRing.GatewayCache.MissCount", Counters.DATAACCESS_NDS_ROUTINGDATARING_GATEWAYCACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.Cache.MapSize", Counters.DATAACCESS_NDS_PHONEMESRING_CACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.Cache.MapCapacity", Counters.DATAACCESS_NDS_PHONEMESRING_CACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.Cache.MapBucketCount", Counters.DATAACCESS_NDS_PHONEMESRING_CACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.Cache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_PHONEMESRING_CACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.Cache.MapElementSize", Counters.DATAACCESS_NDS_PHONEMESRING_CACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.Cache.HitCount", Counters.DATAACCESS_NDS_PHONEMESRING_CACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.Cache.MissCount", Counters.DATAACCESS_NDS_PHONEMESRING_CACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.KeyCache.MapSize", Counters.DATAACCESS_NDS_PHONEMESRING_KEYCACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.KeyCache.MapCapacity", Counters.DATAACCESS_NDS_PHONEMESRING_KEYCACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.KeyCache.MapBucketCount", Counters.DATAACCESS_NDS_PHONEMESRING_KEYCACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.KeyCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_PHONEMESRING_KEYCACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.KeyCache.MapElementSize", Counters.DATAACCESS_NDS_PHONEMESRING_KEYCACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.KeyCache.HitCount", Counters.DATAACCESS_NDS_PHONEMESRING_KEYCACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.KeyCache.MissCount", Counters.DATAACCESS_NDS_PHONEMESRING_KEYCACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.NamedObjectIdsCache.MapSize", Counters.DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.NamedObjectIdsCache.MapCapacity", Counters.DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.NamedObjectIdsCache.MapBucketCount", Counters.DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.NamedObjectIdsCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.NamedObjectIdsCache.MapElementSize", Counters.DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.NamedObjectIdsCache.HitCount", Counters.DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.PhonemesRing.NamedObjectIdsCache.MissCount", Counters.DATAACCESS_NDS_PHONEMESRING_NAMEDOBJECTIDSCACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.OrthoImagesRing.TileCache.MapSize", Counters.DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.OrthoImagesRing.TileCache.MapCapacity", Counters.DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.OrthoImagesRing.TileCache.MapBucketCount", Counters.DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.OrthoImagesRing.TileCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.OrthoImagesRing.TileCache.MapElementSize", Counters.DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.OrthoImagesRing.TileCache.HitCount", Counters.DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.OrthoImagesRing.TileCache.MissCount", Counters.DATAACCESS_NDS_ORTHOIMAGESRING_TILECACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.JunctionViewImagesRing.TileCache.MapSize", Counters.DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.JunctionViewImagesRing.TileCache.MapCapacity", Counters.DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.JunctionViewImagesRing.TileCache.MapBucketCount", Counters.DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.JunctionViewImagesRing.TileCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.JunctionViewImagesRing.TileCache.MapElementSize", Counters.DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.JunctionViewImagesRing.TileCache.HitCount", Counters.DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.JunctionViewImagesRing.TileCache.MissCount", Counters.DATAACCESS_NDS_JUNCTIONVIEWIMAGESRING_TILECACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.TextureMapCache.MapSize", Counters.DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.TextureMapCache.MapCapacity", Counters.DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.TextureMapCache.MapBucketCount", Counters.DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.TextureMapCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.TextureMapCache.MapElementSize", Counters.DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.TextureMapCache.HitCount", Counters.DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.TextureMapCache.MissCount", Counters.DATAACCESS_NDS_OBJECTS3DRING_TEXTUREMAPCACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.SpatialTreeCache.MapSize", Counters.DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.SpatialTreeCache.MapCapacity", Counters.DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.SpatialTreeCache.MapBucketCount", Counters.DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.SpatialTreeCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.SpatialTreeCache.MapElementSize", Counters.DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.SpatialTreeCache.HitCount", Counters.DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.SpatialTreeCache.MissCount", Counters.DATAACCESS_NDS_OBJECTS3DRING_SPATIALTREECACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.BodyGeometryCache.MapSize", Counters.DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.BodyGeometryCache.MapCapacity", Counters.DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.BodyGeometryCache.MapBucketCount", Counters.DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.BodyGeometryCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.BodyGeometryCache.MapElementSize", Counters.DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.BodyGeometryCache.HitCount", Counters.DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.Objects3DRing.BodyGeometryCache.MissCount", Counters.DATAACCESS_NDS_OBJECTS3DRING_BODYGEOMETRYCACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NamesRing.Cache.MapSize", Counters.DATAACCESS_NDS_NAMESRING_CACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NamesRing.Cache.MapCapacity", Counters.DATAACCESS_NDS_NAMESRING_CACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NamesRing.Cache.MapBucketCount", Counters.DATAACCESS_NDS_NAMESRING_CACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NamesRing.Cache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_NAMESRING_CACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NamesRing.Cache.MapElementSize", Counters.DATAACCESS_NDS_NAMESRING_CACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NamesRing.Cache.HitCount", Counters.DATAACCESS_NDS_NAMESRING_CACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NamesRing.Cache.MissCount", Counters.DATAACCESS_NDS_NAMESRING_CACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.AdasRing.TileCache.MapSize", Counters.DATAACCESS_NDS_ADASRING_TILECACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.AdasRing.TileCache.MapCapacity", Counters.DATAACCESS_NDS_ADASRING_TILECACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.AdasRing.TileCache.MapBucketCount", Counters.DATAACCESS_NDS_ADASRING_TILECACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.AdasRing.TileCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_ADASRING_TILECACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.AdasRing.TileCache.MapElementSize", Counters.DATAACCESS_NDS_ADASRING_TILECACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.AdasRing.TileCache.HitCount", Counters.DATAACCESS_NDS_ADASRING_TILECACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.AdasRing.TileCache.MissCount", Counters.DATAACCESS_NDS_ADASRING_TILECACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.GuidanceDataRing.TileCache.MapSize", Counters.DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.GuidanceDataRing.TileCache.MapCapacity", Counters.DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.GuidanceDataRing.TileCache.MapBucketCount", Counters.DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.GuidanceDataRing.TileCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.GuidanceDataRing.TileCache.MapElementSize", Counters.DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.GuidanceDataRing.TileCache.HitCount", Counters.DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.GuidanceDataRing.TileCache.MissCount", Counters.DATAACCESS_NDS_GUIDANCEDATARING_TILECACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.MapAccessBaseRing.Map", Counters.DATAACCESS_NDS_MAPACCESSBASERING_MAP, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.MapAccessBaseRing.Products", Counters.DATAACCESS_NDS_MAPACCESSBASERING_PRODUCTS, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.MapAccessBaseRing.UpdateRegions", Counters.DATAACCESS_NDS_MAPACCESSBASERING_UPDATEREGIONS, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.MapAccessBaseRing.MetadataRegions", Counters.DATAACCESS_NDS_MAPACCESSBASERING_METADATAREGIONS, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NameAttrsRing.TileCache.MapSize", Counters.DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_MAPSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NameAttrsRing.TileCache.MapCapacity", Counters.DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_MAPCAPACITY, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NameAttrsRing.TileCache.MapBucketCount", Counters.DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_MAPBUCKETCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NameAttrsRing.TileCache.MapMaxLoadFactor", Counters.DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_MAPMAXLOADFACTOR, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NameAttrsRing.TileCache.MapElementSize", Counters.DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_MAPELEMENTSIZE, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NameAttrsRing.TileCache.HitCount", Counters.DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_HITCOUNT, 0));
            COUNTERS.add(new MetricInformation("DataAccess.NDS.NameAttrsRing.TileCache.MissCount", Counters.DATAACCESS_NDS_NAMEATTRSRING_TILECACHE_MISSCOUNT, 0));
            COUNTERS.add(new MetricInformation("FreeTextSearch.ComboSearchEngine.MemoryUsage", Counters.FREETEXTSEARCH_COMBOSEARCHENGINE_MEMORYUSAGE, 0));
            COUNTERS.add(new MetricInformation("MapInfo.Query.Count", Counters.MAPINFO_QUERY_COUNT, 0));
            COUNTERS.add(new MetricInformation("MapMatching.PositionUpdate.Delays.GNSSToPE", Counters.MAPMATCHING_POSITIONUPDATE_DELAYS_GNSSTOPE, 0));
            COUNTERS.add(new MetricInformation("MapMatching.PositionUpdate.Delays.PEToMMPos", Counters.MAPMATCHING_POSITIONUPDATE_DELAYS_PETOMMPOS, 0));
            COUNTERS.add(new MetricInformation("MapMatching.PositionUpdate.Delays.GNSSToMMPos", Counters.MAPMATCHING_POSITIONUPDATE_DELAYS_GNSSTOMMPOS, 0));
            COUNTERS.add(new MetricInformation("MapMatching.PathTree.Paths", Counters.MAPMATCHING_PATHTREE_PATHS, 0));
            COUNTERS.add(new MetricInformation("MapMatching.RoadCache.MatchRoads", Counters.MAPMATCHING_ROADCACHE_MATCHROADS, 0));
            COUNTERS.add(new MetricInformation("MapMatching.POIWarning.POIsInStablePool", Counters.MAPMATCHING_POIWARNING_POISINSTABLEPOOL, 0));
            COUNTERS.add(new MetricInformation("MapMatching.MapMatcher.MemoryUsage", Counters.MAPMATCHING_MAPMATCHER_MEMORYUSAGE, 0));
            COUNTERS.add(new MetricInformation("MapMatching.SafetyLocations.InStablePool", Counters.MAPMATCHING_SAFETYLOCATIONS_INSTABLEPOOL, 0));
            COUNTERS.add(new MetricInformation("MapMatching.ADAS.ResetPathCount", Counters.MAPMATCHING_ADAS_RESETPATHCOUNT, 0));
            COUNTERS.add(new MetricInformation("MapMatching.ADAS.UpdatePathCount", Counters.MAPMATCHING_ADAS_UPDATEPATHCOUNT, 0));
            COUNTERS.add(new MetricInformation("MapMatching.RouteDataProvider.BytesReceived", Counters.MAPMATCHING_ROUTEDATAPROVIDER_BYTESRECEIVED, 0));
            COUNTERS.add(new MetricInformation("MapMatching.MapDataProvider.BytesReceived", Counters.MAPMATCHING_MAPDATAPROVIDER_BYTESRECEIVED, 0));
            COUNTERS.add(new MetricInformation("MapMatching.ResultProvider.BytesReceived", Counters.MAPMATCHING_RESULTPROVIDER_BYTESRECEIVED, 0));
            COUNTERS.add(new MetricInformation("MapMatching.PathMatcher.FullStateSize", Counters.MAPMATCHING_PATHMATCHER_FULLSTATESIZE, 0));
            COUNTERS.add(new MetricInformation("MapMatching.PathMatcher.MapDataCacheSize", Counters.MAPMATCHING_PATHMATCHER_MAPDATACACHESIZE, 0));
            COUNTERS.add(new MetricInformation("MapUpdate.UpdateProgress", Counters.MAPUPDATE_UPDATEPROGRESS, 0));
            COUNTERS.add(new MetricInformation("MapUpdate.ValidationProgress", Counters.MAPUPDATE_VALIDATIONPROGRESS, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.BuildLegacyInstructionsCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_BUILDLEGACYINSTRUCTIONSCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.BuildLaneInformationCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_BUILDLANEINFORMATIONCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.CheckForBifurcationCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_CHECKFORBIFURCATIONCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.InvestigateChangeCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_INVESTIGATECHANGECOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.CreateLaneGuidanceInstructionCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_CREATELANEGUIDANCEINSTRUCTIONCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.MotorwayBifurcationOrExitCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_MOTORWAYBIFURCATIONOREXITCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.BifurcationAtPosCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_BIFURCATIONATPOSCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.BuildLaneInfoAndAppendInstructionCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_BUILDLANEINFOANDAPPENDINSTRUCTIONCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.GenerateBifurcationInstructionCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_GENERATEBIFURCATIONINSTRUCTIONCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.GenerateInstructionCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_GENERATEINSTRUCTIONCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.GatherOutgoingLinesCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_GATHEROUTGOINGLINESCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.FillLanesInfoCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_FILLLANESINFOCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.ConnectivityMaxDepthOnUpcomingArcsCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_CONNECTIVITYMAXDEPTHONUPCOMINGARCSCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.LaneMaxDepthOnUpcomingArcsCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_LANEMAXDEPTHONUPCOMINGARCSCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.GetEndLanesCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETENDLANESCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.GetEndLanesIteratorCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETENDLANESITERATORCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.ReorderLanesCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_REORDERLANESCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.GetNumberOfLanesInDrivingDirectionCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETNUMBEROFLANESINDRIVINGDIRECTIONCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.GetLaneIndexCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETLANEINDEXCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.GetLaneMarkingCounter", Counters.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETLANEMARKINGCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.Guidance.RouteInfo.ArcCounter", Counters.ROUTING_GUIDANCE_ROUTEINFO_ARCCOUNTER, 0));
            COUNTERS.add(new MetricInformation("Routing.RoutingEngine.MemoryConsumption", Counters.ROUTING_ROUTINGENGINE_MEMORYCONSUMPTION, 0));
            COUNTERS.add(new MetricInformation("Routing.RoutingEngine.PopCount", Counters.ROUTING_ROUTINGENGINE_POPCOUNT, 0));
            COUNTERS.add(new MetricInformation("Routing.RoutingEngine.MaxLabels", Counters.ROUTING_ROUTINGENGINE_MAXLABELS, 0));
            COUNTERS.add(new MetricInformation("Routing.RoutingEngine.RouteLength", Counters.ROUTING_ROUTINGENGINE_ROUTELENGTH, 0));
            COUNTERS.add(new MetricInformation("Routing.PersonalNetwork.ArcStorageJobArcListSize", Counters.ROUTING_PERSONALNETWORK_ARCSTORAGEJOBARCLISTSIZE, 0));
            COUNTERS.add(new MetricInformation("Routing.PersonalNetwork.SQLiteSteps", Counters.ROUTING_PERSONALNETWORK_SQLITESTEPS, 0));
            COUNTERS.add(new MetricInformation("Routing.PersonalNetwork.SQLiteExecs", Counters.ROUTING_PERSONALNETWORK_SQLITEEXECS, 0));
            COUNTERS.add(new MetricInformation("Routing.PersonalNetwork.TripStretches", Counters.ROUTING_PERSONALNETWORK_TRIPSTRETCHES, 0));
            COUNTERS.add(new MetricInformation("Routing.PersonalNetwork.SplitStretch", Counters.ROUTING_PERSONALNETWORK_SPLITSTRETCH, 0));
            COUNTERS.add(new MetricInformation("Traffic.TrafficTable.MemoryUsedKiB", Counters.TRAFFIC_TRAFFICTABLE_MEMORYUSEDKIB, 0));
            COUNTERS.add(new MetricInformation("SQLiteUtils.MemoryUsed", Counters.SQLITEUTILS_MEMORYUSED, 0));
            COUNTERS.add(new MetricInformation("SQLiteUtils.MemoryHighWater", Counters.SQLITEUTILS_MEMORYHIGHWATER, 0));
            COUNTERS.add(new MetricInformation("System.Test.Internal", Counters.SYSTEM_TEST_INTERNAL, 0));
            COUNTERS.add(new MetricInformation("System.Memory.Usage", Counters.SYSTEM_MEMORY_USAGE, 0));
            COUNTERS.add(new MetricInformation("System.Memory.RSS", Counters.SYSTEM_MEMORY_RSS, 0));
            COUNTERS.add(new MetricInformation("System.Memory.PSS", Counters.SYSTEM_MEMORY_PSS, 0));
            COUNTERS.add(new MetricInformation("System.Memory.Shared", Counters.SYSTEM_MEMORY_SHARED, 0));
            COUNTERS.add(new MetricInformation("System.CPU.Usage", Counters.SYSTEM_CPU_USAGE, 0));
            COUNTERS.add(new MetricInformation("System.IO.OpenFiles", Counters.SYSTEM_IO_OPENFILES, 0));
            COUNTERS.add(new MetricInformation("System.IO.ReadCount", Counters.SYSTEM_IO_READCOUNT, 0));
            COUNTERS.add(new MetricInformation("System.IO.WriteCount", Counters.SYSTEM_IO_WRITECOUNT, 0));
            COUNTERS.add(new MetricInformation("System.IO.ReadBytes", Counters.SYSTEM_IO_READBYTES, 0));
            COUNTERS.add(new MetricInformation("System.IO.WriteBytes", Counters.SYSTEM_IO_WRITEBYTES, 0));
            TIMERS.add(new MetricInformation("IDrivingContextInfo.Query", Timers.IDRIVINGCONTEXTINFO_QUERY, 0));
            TIMERS.add(new MetricInformation("IDrivingContextInfo.QuerySet", Timers.IDRIVINGCONTEXTINFO_QUERYSET, 0));
            TIMERS.add(new MetricInformation("IDrivingContextInfo.CloseQuery", Timers.IDRIVINGCONTEXTINFO_CLOSEQUERY, 0));
            TIMERS.add(new MetricInformation("IDrivingContextInfo.Parse", Timers.IDRIVINGCONTEXTINFO_PARSE, 0));
            TIMERS.add(new MetricInformation("IDrivingContextInfo.Execute", Timers.IDRIVINGCONTEXTINFO_EXECUTE, 0));
            TIMERS.add(new MetricInformation("IDrivingContextInfo.SendResult", Timers.IDRIVINGCONTEXTINFO_SENDRESULT, 0));
            TIMERS.add(new MetricInformation("IDrivingContextInfo.ResultPacker", Timers.IDRIVINGCONTEXTINFO_RESULTPACKER, 0));
            TIMERS.add(new MetricInformation("FreeTextSearch.ComboSearchEngine.Query.Execution.Duration", Timers.FREETEXTSEARCH_COMBOSEARCHENGINE_QUERY_EXECUTION_DURATION, 0));
            TIMERS.add(new MetricInformation("FreeTextSearch.ComboSearchEngine.Query.Execution.Average", Timers.FREETEXTSEARCH_COMBOSEARCHENGINE_QUERY_EXECUTION_AVERAGE, 0));
            TIMERS.add(new MetricInformation("FreeTextSearch.ComboSearchEngine.Query.Execution.Median", Timers.FREETEXTSEARCH_COMBOSEARCHENGINE_QUERY_EXECUTION_MEDIAN, 0));
            TIMERS.add(new MetricInformation("FreeTextSearch.ComboSearchEngine.Query.Execution.Max", Timers.FREETEXTSEARCH_COMBOSEARCHENGINE_QUERY_EXECUTION_MAX, 0));
            TIMERS.add(new MetricInformation("MapInfo.ActiveObject.SingleRunTime", Timers.MAPINFO_ACTIVEOBJECT_SINGLERUNTIME, 0));
            TIMERS.add(new MetricInformation("MapInfo.Query.Duration", Timers.MAPINFO_QUERY_DURATION, 0));
            TIMERS.add(new MetricInformation("MapInfo.ReflectionDispatch.Query.Duration", Timers.MAPINFO_REFLECTIONDISPATCH_QUERY_DURATION, 0));
            TIMERS.add(new MetricInformation("MapMatching.PositionUpdate.ProcessRealPositionUpdate", Timers.MAPMATCHING_POSITIONUPDATE_PROCESSREALPOSITIONUPDATE, 0));
            TIMERS.add(new MetricInformation("MapMatching.PositionUpdate.Observers.MapMatcher", Timers.MAPMATCHING_POSITIONUPDATE_OBSERVERS_MAPMATCHER, 0));
            TIMERS.add(new MetricInformation("MapMatching.PositionUpdate.Observers.Other", Timers.MAPMATCHING_POSITIONUPDATE_OBSERVERS_OTHER, 0));
            TIMERS.add(new MetricInformation("MapMatching.POIWarning.Update", Timers.MAPMATCHING_POIWARNING_UPDATE, 0));
            TIMERS.add(new MetricInformation("MapMatching.POIWarning.Search", Timers.MAPMATCHING_POIWARNING_SEARCH, 0));
            TIMERS.add(new MetricInformation("MapMatching.PositionSimulation.Run", Timers.MAPMATCHING_POSITIONSIMULATION_RUN, 0));
            TIMERS.add(new MetricInformation("MapMatching.PositionSimulation.GenerateInput", Timers.MAPMATCHING_POSITIONSIMULATION_GENERATEINPUT, 0));
            TIMERS.add(new MetricInformation("MapMatching.PositionSimulation.GenerateRouteDemo", Timers.MAPMATCHING_POSITIONSIMULATION_GENERATEROUTEDEMO, 0));
            TIMERS.add(new MetricInformation("MapMatching.PositionSimulation.PositionUpdate", Timers.MAPMATCHING_POSITIONSIMULATION_POSITIONUPDATE, 0));
            TIMERS.add(new MetricInformation("MapMatching.SafetyLocations.Run", Timers.MAPMATCHING_SAFETYLOCATIONS_RUN, 0));
            TIMERS.add(new MetricInformation("MapMatching.SafetyLocations.Update", Timers.MAPMATCHING_SAFETYLOCATIONS_UPDATE, 0));
            TIMERS.add(new MetricInformation("MapMatching.SafetyLocations.Filter", Timers.MAPMATCHING_SAFETYLOCATIONS_FILTER, 0));
            TIMERS.add(new MetricInformation("MapMatching.SafetyLocations.Sync", Timers.MAPMATCHING_SAFETYLOCATIONS_SYNC, 0));
            TIMERS.add(new MetricInformation("MapMatching.ADAS.ResetPath", Timers.MAPMATCHING_ADAS_RESETPATH, 0));
            TIMERS.add(new MetricInformation("MapMatching.ADAS.UpdatePath", Timers.MAPMATCHING_ADAS_UPDATEPATH, 0));
            TIMERS.add(new MetricInformation("MapMatching.PathMatcher.OnPositionUpdate", Timers.MAPMATCHING_PATHMATCHER_ONPOSITIONUPDATE, 0));
            TIMERS.add(new MetricInformation("MapMatching.PathMatcher.PathMatchingStep", Timers.MAPMATCHING_PATHMATCHER_PATHMATCHINGSTEP, 0));
            TIMERS.add(new MetricInformation("MapMatching.PathMatcher.ArcMatchingStep", Timers.MAPMATCHING_PATHMATCHER_ARCMATCHINGSTEP, 0));
            TIMERS.add(new MetricInformation("MapMatching.PathMatcher.ResultPathMatching", Timers.MAPMATCHING_PATHMATCHER_RESULTPATHMATCHING, 0));
            TIMERS.add(new MetricInformation("MapMatching.PathMatcher.SubMapTimer", Timers.MAPMATCHING_PATHMATCHER_SUBMAPTIMER, 0));
            TIMERS.add(new MetricInformation("MapUpdate.CommitPhase", Timers.MAPUPDATE_COMMITPHASE, 0));
            TIMERS.add(new MetricInformation("MapUpdate.ValidationPhase", Timers.MAPUPDATE_VALIDATIONPHASE, 0));
            TIMERS.add(new MetricInformation("MapUpdate.ActiveObject.SingleRunTime", Timers.MAPUPDATE_ACTIVEOBJECT_SINGLERUNTIME, 0));
            TIMERS.add(new MetricInformation("MapVis.SceneRenderer.PeriodicalFrameTimer", Timers.MAPVIS_SCENERENDERER_PERIODICALFRAMETIMER, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.BuildLegacyInstructions", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_BUILDLEGACYINSTRUCTIONS, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.BuildLaneInformation", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_BUILDLANEINFORMATION, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.BuildNavKitInstructions", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_BUILDNAVKITINSTRUCTIONS, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.PopulatePolyline", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_POPULATEPOLYLINE, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.CheckForBifurcation", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_CHECKFORBIFURCATION, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.InvestigateChange", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_INVESTIGATECHANGE, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.CreateLaneGuidanceInstruction", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_CREATELANEGUIDANCEINSTRUCTION, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.MotorwayBifurcationOrExit", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_MOTORWAYBIFURCATIONOREXIT, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.BifurcationAtPos", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_BIFURCATIONATPOS, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.BuildLaneInfoAndAppendInstruction", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_BUILDLANEINFOANDAPPENDINSTRUCTION, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.GenerateBifurcationInstruction", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_GENERATEBIFURCATIONINSTRUCTION, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.GenerateInstruction", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_GENERATEINSTRUCTION, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilder.GatherOutgoingLines", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDER_GATHEROUTGOINGLINES, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.FillLanesInfo", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_FILLLANESINFO, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.ConnectivityMaxDepthOnUpcomingArcs", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_CONNECTIVITYMAXDEPTHONUPCOMINGARCS, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.LaneMaxDepthOnUpcomingArcs", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_LANEMAXDEPTHONUPCOMINGARCS, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.GetEndLanes", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETENDLANES, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.GetEndLanesIterator", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETENDLANESITERATOR, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.ReorderLanes", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_REORDERLANES, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.GetNumberOfLanesInDrivingDirection", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETNUMBEROFLANESINDRIVINGDIRECTION, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.GetLaneIndex", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETLANEINDEX, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Instructions.InstructionBuilderUtils.GetLaneMarking", Timers.ROUTING_GUIDANCE_INSTRUCTIONS_INSTRUCTIONBUILDERUTILS_GETLANEMARKING, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.RouteInfo.RouteInfoSignpostsComputation", Timers.ROUTING_GUIDANCE_ROUTEINFO_ROUTEINFOSIGNPOSTSCOMPUTATION, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.RouteInfo.AnalyzeComplexManeuvers", Timers.ROUTING_GUIDANCE_ROUTEINFO_ANALYZECOMPLEXMANEUVERS, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.RouteInfo.GetSignposts", Timers.ROUTING_GUIDANCE_ROUTEINFO_GETSIGNPOSTS, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.RouteInfo.CheckRoadNameOrNumberChanges", Timers.ROUTING_GUIDANCE_ROUTEINFO_CHECKROADNAMEORNUMBERCHANGES, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.RouteInfo.GetRoadNames", Timers.ROUTING_GUIDANCE_ROUTEINFO_GETROADNAMES, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.RouteInfo.FindConnectingRoads", Timers.ROUTING_GUIDANCE_ROUTEINFO_FINDCONNECTINGROADS, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Route.RouteActivation", Timers.ROUTING_GUIDANCE_ROUTE_ROUTEACTIVATION, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.Route.Release", Timers.ROUTING_GUIDANCE_ROUTE_RELEASE, 0));
            TIMERS.add(new MetricInformation("Routing.Guidance.SideRoads.SideRoadsComputation", Timers.ROUTING_GUIDANCE_SIDEROADS_SIDEROADSCOMPUTATION, 0));
            TIMERS.add(new MetricInformation("Routing.RoutingScheduler.*.InitialTimeSliceDuration", Timers.ROUTING_ROUTINGSCHEDULER_x_INITIALTIMESLICEDURATION, 1));
            TIMERS.add(new MetricInformation("Routing.RoutingScheduler.*.TimeSliceDuration", Timers.ROUTING_ROUTINGSCHEDULER_x_TIMESLICEDURATION, 1));
            TIMERS.add(new MetricInformation("Routing.RoutingEngine.RouteComputation", Timers.ROUTING_ROUTINGENGINE_ROUTECOMPUTATION, 0));
            TIMERS.add(new MetricInformation("Routing.RoutingEngine.RouteComputationDuration", Timers.ROUTING_ROUTINGENGINE_ROUTECOMPUTATIONDURATION, 0));
            TIMERS.add(new MetricInformation("Routing.RoutingEngine.ReachabilitySearchDuration", Timers.ROUTING_ROUTINGENGINE_REACHABILITYSEARCHDURATION, 0));
            TIMERS.add(new MetricInformation("Routing.RoutingEngine.LocationResolution", Timers.ROUTING_ROUTINGENGINE_LOCATIONRESOLUTION, 0));
            TIMERS.add(new MetricInformation("Routing.RoutingEngine.CreateTask", Timers.ROUTING_ROUTINGENGINE_CREATETASK, 0));
            TIMERS.add(new MetricInformation("Routing.RoutingEngine.SubmitTask", Timers.ROUTING_ROUTINGENGINE_SUBMITTASK, 0));
            TIMERS.add(new MetricInformation("Routing.RoutingEngine.FirstComputeCall", Timers.ROUTING_ROUTINGENGINE_FIRSTCOMPUTECALL, 0));
            TIMERS.add(new MetricInformation("Routing.PersonalNetwork.ArcStorageJobDuration", Timers.ROUTING_PERSONALNETWORK_ARCSTORAGEJOBDURATION, 0));
            TIMERS.add(new MetricInformation("Routing.PersonalNetwork.ActiveObject", Timers.ROUTING_PERSONALNETWORK_ACTIVEOBJECT, 0));
            TIMERS.add(new MetricInformation("Engines.System.OnMapUpdated", Timers.ENGINES_SYSTEM_ONMAPUPDATED, 0));
            TIMERS.add(new MetricInformation("Engines.System.MapUpdate.UnloadMap", Timers.ENGINES_SYSTEM_MAPUPDATE_UNLOADMAP, 0));
            TIMERS.add(new MetricInformation("Engines.System.MapUpdate.ReloadMap", Timers.ENGINES_SYSTEM_MAPUPDATE_RELOADMAP, 0));
            TIMERS.add(new MetricInformation("Traffic.Decoding.TrafficFetcher.UpdateTrafficCacheWithEvent.Decode", Timers.TRAFFIC_DECODING_TRAFFICFETCHER_UPDATETRAFFICCACHEWITHEVENT_DECODE, 0));
            TIMERS.add(new MetricInformation("Traffic.Decoding.TTLRTransformer.DecodeLocation", Timers.TRAFFIC_DECODING_TTLRTRANSFORMER_DECODELOCATION, 0));
            TIMERS.add(new MetricInformation("Traffic.Decoding.TMCTransformer.DecodeLocation", Timers.TRAFFIC_DECODING_TMCTRANSFORMER_DECODELOCATION, 0));
            TIMERS.add(new MetricInformation("Traffic.TrafficInHorizon.CPU", Timers.TRAFFIC_TRAFFICINHORIZON_CPU, 0));
            TIMERS.add(new MetricInformation("Traffic.TrafficInHorizon.Wall", Timers.TRAFFIC_TRAFFICINHORIZON_WALL, 0));
            TIMERS.add(new MetricInformation("Traffic.TMC.LoadTables", Timers.TRAFFIC_TMC_LOADTABLES, 0));
        }

        @Override
        public MetricInformations GetCounters() {
            return COUNTERS;
        }

        @Override
        public MetricInformations GetTimers() {
            return TIMERS;
        }

        @Override
        public MetricInformations GetCheckpoints() {
            return CHECKPOINTS;
        }
    }
}
