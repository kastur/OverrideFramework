.class final Landroid/app/ContextImpl$Override;
.super Landroid/app/ContextImpl$ServiceFetcher;
.source "ContextImpl.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingClass;
    value = Landroid/app/ContextImpl;
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x8
    name = null
.end annotation


# direct methods
.method constructor <init>()V
    .locals 0

    .prologue
    .line 415
    invoke-direct {p0}, Landroid/app/ContextImpl$ServiceFetcher;-><init>()V

    return-void
.end method


# virtual methods
.method public createService(Landroid/app/ContextImpl;)Ljava/lang/Object;
    .locals 2
    .parameter "ctx"

    .prologue
    .line 417
    const-string v1, "location"
    invoke-static {v1}, Landroid/os/ServiceManager;->getService(Ljava/lang/String;)Landroid/os/IBinder;
    move-result-object v0
    .local v0, binder:Landroid/os/IBinder;

    invoke-static {v0}, Landroid/location/ILocationManager$Stub;->asInterface(Landroid/os/IBinder;)Landroid/location/ILocationManager;
    move-result-object v1

    new-instance v0, Landroid/location/OverrideLocationManager;
    invoke-direct {v0, p1, v1}, Landroid/location/OverrideLocationManager;-><init>(Landroid/content/Context;Landroid/location/ILocationManager;)V

    return-object v0
.end method
